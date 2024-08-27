package com.prj.furni_shop.modules.product.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.product.dto.request.ProductDto;
import com.prj.furni_shop.modules.product.dto.request.ProductImageUploadDto;
import com.prj.furni_shop.modules.product.dto.response.ProductImageResonse;
import com.prj.furni_shop.modules.product.dto.response.ProductResponse;
import com.prj.furni_shop.modules.product.service.AdminProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin: Quản lý sản phẩm", description = "APIs liên quan đến quản lý sản phẩm")
public class AdminProductController {

    AdminProductService adminProductService;

    @Operation(summary = "Lấy tất cả sản phẩm", description = "Lấy danh sách tất cả sản phẩm (bao gồm sản phẩm bị vô hiệu hóa)")
    @GetMapping()
    public ApiResponse<PaginationWrapper<ProductResponse>> getAllProductsForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return ApiResponse.<PaginationWrapper<ProductResponse>>builder()
                .result(adminProductService.getAllProductsForAdmin(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Thêm sản phẩm mới", description = "Thêm sản phẩm mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/addNewProduct")
    public ApiResponse<ProductResponse> createProduct(
            @RequestBody ProductDto request
    ) {
        return ApiResponse.<ProductResponse>builder()
                .result(adminProductService.createProduct(request))
                .build();
    }

    @Operation(summary = "Thêm ảnh cho sản phẩm mới", description = "Thêm ảnh cho sản phẩm mới với các ảnh được cung cấp")
    @PostMapping(value = "/upload-image", consumes = { "multipart/form-data" })
    public ApiResponse<String> uploadProductImages(
            @RequestParam("productId") int productId,
            @RequestPart("images") List<MultipartFile> images
    ) {
        try {
            ProductImageUploadDto dto = new ProductImageUploadDto();
            dto.setProductId(productId);
            dto.setImages(images);
            adminProductService.uploadImages(dto);
            return ApiResponse.<String>builder()
                    .message("Images uploaded successfully")
                    .build();
        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                    .message("Error uploading avatar: " + e.getMessage())
                    .build();
        }
    }

    @Operation(summary = "Xóa ảnh sản phẩm", description = "Xóa ảnh sản phẩm")
    @DeleteMapping(value = "/delete-image/{productImageId}")
    public ApiResponse<String> deleteProductImage(
            @PathVariable int productImageId
    ) {
        try {
            return ApiResponse.<String>builder()
                    .message(adminProductService.deleteProductImage(productImageId))
                    .build();
        } catch (IOException e) {
            return ApiResponse.<String>builder()
                    .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                    .message("Error delete image: " + e.getMessage())
                    .build();
        }
    }

    @Operation(summary = "Lấy tất cả ảnh liên quan đến sản phẩm", description = "Lấy tất cả ảnh liên quan đến sản phẩm")
    @GetMapping("/productImages/{productId}")
    public ApiResponse<ProductImageResonse> getProductImagesByProductId(
            @PathVariable int productId
    ) {
        return ApiResponse.<ProductImageResonse>builder()
                .result(adminProductService.getProductImagesByProductId(productId))
                .build();
    }

    @Operation(summary = "Sửa sản phẩm", description = "Sửa 1 sản phẩm")
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponse> updateProduct(
            @PathVariable int productId,
            @RequestBody ProductDto request){
        return ApiResponse.<ProductResponse>builder()
                .result(adminProductService.updateProduct(productId, request))
                .build();
    }

    @Operation(summary = "Xóa sản phẩm", description = "Xóa 1 sản phẩm")
    @DeleteMapping("/{productId}")
    public ApiResponse<String> deleteProduct(
            @PathVariable int productId
    ){
        return ApiResponse.<String>builder()
                .message(adminProductService.deleteProduct(productId))
                .build();
    }

    @Operation(summary = "Vô hiệu hóa sản phẩm", description = "Vô hiệu hóa 1 sản phẩm")
    @PutMapping("/disable/{productId}")
    public ApiResponse<String> disableProduct(
            @PathVariable int productId
    ){
        return ApiResponse.<String>builder()
                .message(adminProductService.disableProduct(productId))
                .build();
    }
}
