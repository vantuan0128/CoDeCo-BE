package com.prj.furni_shop.modules.voucher.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.voucher.dto.request.CheckValidVoucherRequest;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import com.prj.furni_shop.modules.voucher.dto.request.CollectRequest;
import com.prj.furni_shop.modules.voucher.dto.response.VoucherResponse;
import com.prj.furni_shop.modules.voucher.entity.UserVoucher;
import com.prj.furni_shop.modules.voucher.entity.Voucher;
import com.prj.furni_shop.modules.voucher.mapper.VoucherMapper;
import com.prj.furni_shop.modules.voucher.repository.UserVoucherRepository;
import com.prj.furni_shop.modules.voucher.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserVoucherService {

    UserRepository userRepository;

    VoucherRepository voucherRepository;

    UserVoucherRepository userVoucherRepository;

    VoucherMapper voucherMapper;
    @Transactional
    public String collectVoucher(CollectRequest request) {
        String code = request.getCode();
        Voucher voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        if(voucher.getQuantity() == 0){
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);
        }
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));


        UserVoucher userVoucher = userVoucherRepository
                .findByUserIdAndVoucherId(user.getUserId(), voucher.getVoucherId())
                .orElse(UserVoucher.builder()
                        .userId(user.getUserId())
                        .voucherId(voucher.getVoucherId())
                        .isUsed(false)
                        .build());

        if (userVoucher.getUserVoucherId() == null) {
            voucher.setQuantity(voucher.getQuantity()-1);
            voucherRepository.save(voucher);
            userVoucherRepository.save(userVoucher);
            return "Success";
        } else {
            throw new AppException(ErrorCode.EXISTED);
        }
    }

    public PaginationWrapper<VoucherResponse> getAllVoucher(int page, int pageSize, String sortBy, String direction, String status){
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        List<UserVoucher> userVouchers = user.getUserVouchers();
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<UserVoucher> userVoucherPages = switch (status) {
            case "all" -> userVoucherRepository.findByUserId(userId, pageable);
            case "unused" -> userVoucherRepository.findUnusedVouchersByUserId(userId, pageable);
            case "used" -> userVoucherRepository.findUsedVouchersByUserId(userId, pageable);
            case "expired" -> userVoucherRepository.findExpiredVouchersByUserId(userId, pageable);
            default -> throw new AppException(ErrorCode.INVALID_INPUT_DATA);
        };


        var voucherResponses = userVoucherPages.getContent().stream()
                .map(UserVoucher::getVoucher)
                .map(voucherMapper::toVoucherResponse)
                .toList();

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(userVoucherPages.getTotalElements())
                .totalPages((int) Math.ceil((double) userVoucherPages.getTotalElements() / pageSize))
                .hasNext(userVoucherPages.hasNext())
                .hasPrevious(userVoucherPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(voucherResponses, paginationInfo);
    }

    public PaginationWrapper<VoucherResponse> getAll(int page, int pageSize, String sortBy, String direction)
    {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Voucher> voucherPages = voucherRepository.findAll(pageable);
        var voucherResponses = voucherPages.getContent().stream()
                .map(voucherMapper::toVoucherResponse)
                .toList();

        PaginationInfo paginationInfo = PaginationInfo.builder()
                .totalCount(voucherPages.getTotalElements())
                .totalPages((int) Math.ceil((double) voucherPages.getTotalElements() / pageSize))
                .hasNext(voucherPages.hasNext())
                .hasPrevious(voucherPages.hasPrevious())
                .build();

        return new PaginationWrapper<>(voucherResponses, paginationInfo);
    }

    public Double checkValidVoucher(CheckValidVoucherRequest request) {
        var context = SecurityContextHolder.getContext();
        int userId = Integer.parseInt(context.getAuthentication().getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        Voucher voucher = voucherRepository.findById(request.getVoucherId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));

        UserVoucher userVoucher = userVoucherRepository.findByUserIdAndVoucherId(userId, voucher.getVoucherId())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

        if (userVoucher.getIsUsed()) throw new AppException(ErrorCode.VOUCHER_CONDITION_NOT_MET);

        if (voucher.getQuantity() == 0)
            throw new AppException(ErrorCode.INSUFFICIENT_STOCK);

        if (voucher.getStartDate() != null && voucher.getStartDate().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VOUCHER_CONDITION_NOT_MET);
        }

        if (voucher.getEndDate() != null && voucher.getEndDate().isBefore(LocalDateTime.now()))
            throw new AppException(ErrorCode.VOUCHER_CONDITION_NOT_MET);

        if (voucher.getMinValueOrder() > request.getTotalMoney())
            throw new AppException(ErrorCode.VOUCHER_CONDITION_NOT_MET);

        double discount = request.getTotalMoney() * (voucher.getDiscountPercent() / 100);
        if (voucher.getMaxValueDiscount() != null && discount > voucher.getMaxValueDiscount()) {
            discount = voucher.getMaxValueDiscount();
        }
        return discount;
    }
}
