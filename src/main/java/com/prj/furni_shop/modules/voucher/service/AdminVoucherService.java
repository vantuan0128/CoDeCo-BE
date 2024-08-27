package com.prj.furni_shop.modules.voucher.service;

import com.prj.furni_shop.common.PaginationInfo;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.voucher.dto.request.VoucherRequest;
import com.prj.furni_shop.modules.voucher.dto.response.VoucherResponse;
import com.prj.furni_shop.modules.voucher.entity.Voucher;
import com.prj.furni_shop.modules.voucher.mapper.VoucherMapper;
import com.prj.furni_shop.modules.voucher.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminVoucherService {
    VoucherRepository voucherRepository;
    VoucherMapper voucherMapper;

    public VoucherResponse create(VoucherRequest request){
        if(voucherRepository.existsByCode(request.getCode())) throw new AppException(ErrorCode.EXISTED);
        Voucher voucher = voucherMapper.toVoucher(request);
        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
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

    public VoucherResponse update(int voucherId, VoucherRequest request){
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        voucherMapper.updateVoucher(voucher,request);
        return voucherMapper.toVoucherResponse(voucherRepository.save(voucher));
    }

    public String delete(int voucherId){
        if(!voucherRepository.existsById(voucherId))throw new AppException(ErrorCode.NOT_EXISTED);
        voucherRepository.deleteById(voucherId);
        return "Success";
    }

    public VoucherResponse getOne(int voucherId) {
        var voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTED));
        return voucherMapper.toVoucherResponse(voucher);
    }
}
