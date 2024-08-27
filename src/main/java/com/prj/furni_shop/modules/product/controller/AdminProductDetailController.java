package com.prj.furni_shop.modules.product.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.product.dto.request.ProductDetailDto;
import com.prj.furni_shop.modules.product.dto.response.ProductDetailResponse;

import com.prj.furni_shop.modules.product.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/productDetails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý chi tiết sản phẩm", description = "APIs liên quan đến quản lý chi tiết sản phẩm")
public class AdminProductDetailController {

    AdminProductService adminProductService;

    @Operation(summary = "Lấy tất cả chi tiết sản phẩm", description = "Lấy tất cả các chi tiết sản phẩm")
    @GetMapping()
    public ApiResponse<PaginationWrapper<ProductDetailResponse>> getAllProductDetails(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ApiResponse.<PaginationWrapper<ProductDetailResponse>>builder()
                .result(adminProductService.getAllProductDetails(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Lấy tất cả chi tiết sản phẩm của 1 sản phẩm", description = "Lấy tất cả chi tiết sản phẩm của 1 sản phẩm")
    @GetMapping("/{productId}")
    public ApiResponse<List<ProductDetailResponse>> getAllProductDetailsByProductId(
            @PathVariable int productId
    ) {
        return ApiResponse.<List<ProductDetailResponse>>builder()
                .result(adminProductService.getAllProductDetailsByProductId(productId))
                .build();
    }

    @Operation(summary = "Thêm chi tiết sản phẩm mới", description = "Thêm sản phẩm mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/addNewProductDetail")
    public ApiResponse<ProductDetailResponse> createProduct(
            @RequestBody ProductDetailDto request
    ) {
        return ApiResponse.<ProductDetailResponse>builder()
                .result(adminProductService.createProductDetail(request))
                .build();
    }

    @Operation(summary = "Sửa 1 chi tiết sản phẩm", description = "Sửa 1 chi tiết sản phẩm")
    @PutMapping("/{productDetailId}")
    public ApiResponse<String> updateProductDetail(
            @PathVariable int productDetailId,
            @RequestBody ProductDetailDto request){
        return ApiResponse.<String>builder()
                .message(adminProductService.updateProductDetail(productDetailId, request))
                .build();
    }

    @Operation(summary = "Xóa 1 chi tiết sản phẩm", description = "Xóa 1 chi tiết sản phẩm")
    @DeleteMapping("/{productDetailId}")
    public ApiResponse<String> deleteProductDetail(
            @PathVariable int productDetailId
    ){
        return ApiResponse.<String>builder()
                .message(adminProductService.deleteProductDetail(productDetailId))
                .build();
    }

}
