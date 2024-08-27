package com.prj.furni_shop.modules.voucher.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.voucher.dto.request.VoucherRequest;
import com.prj.furni_shop.modules.voucher.dto.response.VoucherResponse;
import com.prj.furni_shop.modules.voucher.service.AdminVoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/vouchers/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý voucher", description = "APIs liên quan đến quản lý voucher")
public class AdminVoucherController {

    AdminVoucherService service;

    @Operation(summary = "Lấy tất cả voucher có sẵn", description = "Lấy thông tin tất cả voucher có sẵn")
    @GetMapping("/get-all")
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

    @Operation(summary = "Lấy 1 voucher", description = "Lấy thông tin 1 voucher dựa trên thông tin được cung cấp")
    @GetMapping("/get/{voucherId}")
    public ApiResponse<VoucherResponse> getOneVoucher(
            @PathVariable int voucherId
    ) {
        return ApiResponse.<VoucherResponse>builder()
                .result(service.getOne(voucherId))
                .build();
    }

    @Operation(summary = "Thêm voucher", description = "Tạo voucher mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/create")
    public ApiResponse<VoucherResponse> create(
            @RequestBody VoucherRequest request
    ) {
        return ApiResponse.<VoucherResponse>builder()
                .result(service.create(request))
                .build();
    }

    @Operation(summary = "Cập nhật voucher", description = "Cập nhật các thông tin của voucher")
    @PutMapping("/update/{voucherId}")
    public ApiResponse<VoucherResponse> update(
            @RequestBody VoucherRequest request,
            @PathVariable int voucherId
    ) {
        return ApiResponse.<VoucherResponse>builder()
                .result(service.update(voucherId,request))
                .build();
    }

    @Operation(summary = "Xóa voucher theo ID", description = "Xóa voucher theo ID được cung cấp")
    @DeleteMapping("/delete/{voucherId}")
    public ApiResponse<Void> delete(@PathVariable int voucherId) {
        return ApiResponse.<Void>builder()
                .message(service.delete(voucherId))
                .build();
    }
}