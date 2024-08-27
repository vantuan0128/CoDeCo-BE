package com.prj.furni_shop.modules.product.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.product.dto.request.SaleDto;
import com.prj.furni_shop.modules.product.dto.response.SaleResponse;
import com.prj.furni_shop.modules.product.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý giảm giá", description = "APIs liên quan đến quản lý giảm giá")
public class AdminSaleController {

    SaleService saleService;

    @Operation(summary = "Tạo mới giảm giá", description = "Tạo mới giảm giá")
    @PostMapping()
    public ApiResponse<SaleResponse> createSale(
            @RequestBody SaleDto request
            ) {
        return ApiResponse.<SaleResponse>builder()
                .result(saleService.createSale(request))
                .build();
    }

    @Operation(summary = "Lấy tất cả giảm giá", description = "Lấy tất cả giảm giá")
    @GetMapping()
    public ApiResponse<PaginationWrapper<SaleResponse>> getAllSales(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "saleId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<SaleResponse>>builder()
                .result(saleService.getAllSales(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Lấy 1 giảm giá", description = "Lấy thông tin 1 giảm giá dựa trên thông tin được cung cấp")
    @GetMapping("/{saleId}")
    public ApiResponse<SaleResponse> getOneSale(
            @PathVariable int saleId
    ) {
        return ApiResponse.<SaleResponse>builder()
                .result(saleService.getOneSale(saleId))
                .build();
    }

    @Operation(summary = "Xóa 1 giảm giá", description = "Xóa 1 giảm giá dựa trên thông tin được cung cấp")
    @DeleteMapping("/{saleId}")
    public ApiResponse<String> deleteSale(
            @PathVariable int saleId
    ) {
        return ApiResponse.<String>builder()
                .result(saleService.deleteSale(saleId))
                .build();
    }

    @Operation(summary = "Sửa 1 giảm giá", description = "Sửa 1 giảm giá dựa trên thông tin được cung cấp")
    @PutMapping("/{saleId}")
    public ApiResponse<SaleResponse> updateSale(
            @PathVariable int saleId,
            @RequestBody SaleDto request
    ) {
        return ApiResponse.<SaleResponse>builder()
                .result(saleService.updateSale(saleId, request))
                .build();
    }
}
