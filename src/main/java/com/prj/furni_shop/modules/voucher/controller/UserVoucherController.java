package com.prj.furni_shop.modules.voucher.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.voucher.dto.request.CheckValidVoucherRequest;
import com.prj.furni_shop.modules.voucher.dto.request.CollectRequest;
import com.prj.furni_shop.modules.voucher.dto.response.VoucherResponse;
import com.prj.furni_shop.modules.voucher.service.UserVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vouchers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Voucher", description = "APIs liên quan đến voucher của người dùng")
public class UserVoucherController {

    UserVoucherService service;

    @Operation(summary = "Lấy tất cả voucher có sẵn", description = "Lấy thông tin tất cả voucher có sẵn")
    @GetMapping("/get-all-voucher")
    public ApiResponse<PaginationWrapper<VoucherResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "voucherId") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<VoucherResponse>>builder()
                .result(service.getAll(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Lấy tất cả voucher", description = "Lấy thông tin tất cả voucher có sẵn theo các trạng thái all, unused, used, expired")
    @GetMapping("/get-all")
    public ApiResponse<PaginationWrapper<VoucherResponse>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "userVoucherId") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(defaultValue = "all") String status
    ) {
        return ApiResponse.<PaginationWrapper<VoucherResponse>>builder()
                .result(service.getAllVoucher(page, pageSize, sortBy, direction, status))
                .build();
    }

    @Operation(summary = "Thu thập 1 voucher", description = "Thu thập voucher mới")
    @PostMapping("/collect")
    public ApiResponse<Void> collect(
            @RequestBody CollectRequest request
    ) {
        return ApiResponse.<Void>builder()
                .message(service.collectVoucher(request))
                .build();
    }

    @Operation(summary = "Kiểm tra tính hợp lệ của voucher", description = "Kiểm tra tính hợp lệ của voucher với tổng đơn hàng, nếu đúng thì trả về giảm giá")
    @PostMapping("/check-voucher")
    public ApiResponse<Double> checkValidVoucher(
            @RequestBody CheckValidVoucherRequest request
    ) {
        return ApiResponse.<Double>builder()
                .result(service.checkValidVoucher(request))
                .build();
    }
}