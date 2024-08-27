package com.prj.furni_shop.modules.category.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.category.dto.request.CategoryDto;
import com.prj.furni_shop.modules.category.dto.request.ColorDto;
import com.prj.furni_shop.modules.category.dto.request.MaterialDto;
import com.prj.furni_shop.modules.category.dto.request.SizeDto;
import com.prj.furni_shop.modules.category.dto.response.CategoryResponse;
import com.prj.furni_shop.modules.category.dto.response.ColorResponse;
import com.prj.furni_shop.modules.category.dto.response.MaterialResponse;
import com.prj.furni_shop.modules.category.dto.response.SizeResponse;
import com.prj.furni_shop.modules.category.service.CategoryService;
import com.prj.furni_shop.modules.category.service.ColorService;
import com.prj.furni_shop.modules.category.service.MaterialService;
import com.prj.furni_shop.modules.category.service.SizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Admin: Quản lý danh mục", description = "APIs liên quan đến quản lý danh mục sản phẩm")
public class CategoryController {
    CategoryService categoryService;
    ColorService colorService;
    SizeService sizeService;
    MaterialService materialService;

    @Operation(summary = "Lấy tất cả danh mục có sẵn", description = "Lấy thông tin tất cả danh mục có sẵn")
    @GetMapping("/category")
    public ApiResponse<PaginationWrapper<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "categoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String dierction
    ) {
        return ApiResponse.<PaginationWrapper<CategoryResponse>>builder()
                .result(categoryService.getAllCategories(page, pageSize, sortBy, dierction))
                .build();
    }

    @Operation(summary = "Lấy tất cả màu sắc có sẵn", description = "Lấy thông tin tất cả màu sắc có sẵn")
    @GetMapping("/color")
    public ApiResponse<PaginationWrapper<ColorResponse>> getAllColors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "colorId") String sortBy,
            @RequestParam(defaultValue = "asc") String dierction
    ) {
        return ApiResponse.<PaginationWrapper<ColorResponse>>builder()
                .result(colorService.getAllColors(page, pageSize, sortBy, dierction))
                .build();
    }

    @Operation(summary = "Lấy tất cả kích cỡ có sẵn", description = "Lấy thông tin tất cả kích cỡ có sẵn")
    @GetMapping("/size")
    public ApiResponse<PaginationWrapper<SizeResponse>> getAllSizes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "sizeId") String sortBy,
            @RequestParam(defaultValue = "asc") String dierction
    ) {
        return ApiResponse.<PaginationWrapper<SizeResponse>>builder()
                .result(sizeService.getAllSizes(page, pageSize, sortBy, dierction))
                .build();
    }

    @Operation(summary = "Lấy tất cả chất liệu có sẵn", description = "Lấy thông tin tất cả chất liệu có sẵn")
    @GetMapping("/material")
    public ApiResponse<PaginationWrapper<MaterialResponse>> getAllMaterials(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "materialId") String sortBy,
            @RequestParam(defaultValue = "asc") String dierction
    ) {
        return ApiResponse.<PaginationWrapper<MaterialResponse>>builder()
                .result(materialService.getAllMaterials(page, pageSize, sortBy, dierction))
                .build();
    }

    @Operation(summary = "Thêm danh mục", description = "Tạo danh mục mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/add-new-category")
    public ApiResponse<CategoryResponse> createCategory(
            @RequestBody CategoryDto request
    ) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @Operation(summary = "Thêm màu sắc", description = "Tạo màu sắc mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/add-new-color")
    public ApiResponse<ColorResponse> createColor(
            @RequestBody ColorDto request
    ) {
        return ApiResponse.<ColorResponse>builder()
                .result(colorService.createColor(request))
                .build();
    }

    @Operation(summary = "Thêm kích cỡ", description = "Tạo kích cỡ mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/add-new-size")
    public ApiResponse<SizeResponse> createSize(
            @RequestBody SizeDto request
    ) {
        return ApiResponse.<SizeResponse>builder()
                .result(sizeService.createSize(request))
                .build();
    }

    @Operation(summary = "Thêm chất liệu", description = "Tạo chất liệu mới với các thông tin chi tiết được cung cấp")
    @PostMapping("/add-new-material")
    public ApiResponse<MaterialResponse> createMaterial(
            @RequestBody MaterialDto request
    ) {
        return ApiResponse.<MaterialResponse>builder()
                .result(materialService.createMaterial(request))
                .build();
    }

    @Operation(summary = "Cập nhật danh mục", description = "Cập nhật các thông tin của danh mục")
    @PutMapping("/category/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(
            @RequestBody CategoryDto request,
            @PathVariable int categoryId
    ) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(categoryId,request))
                .build();
    }

    @Operation(summary = "Cập nhật màu sắc", description = "Cập nhật các thông tin của màu sắc")
    @PutMapping("/color/{colorId}")
    public ApiResponse<ColorResponse> updateColor(
            @RequestBody ColorDto request,
            @PathVariable int colorId
    ) {
        return ApiResponse.<ColorResponse>builder()
                .result(colorService.updateColor(colorId,request))
                .build();
    }

    @Operation(summary = "Cập nhật kích cỡ", description = "Cập nhật các thông tin của kích cỡ")
    @PutMapping("/size/{sizeId}")
    public ApiResponse<SizeResponse> updateSize(
            @RequestBody SizeDto request,
            @PathVariable int sizeId
    ) {
        return ApiResponse.<SizeResponse>builder()
                .result(sizeService.updateSize(sizeId,request))
                .build();
    }

    @Operation(summary = "Cập nhật chất liệu", description = "Cập nhật các thông tin của chất liệu")
    @PutMapping("/material/{materialId}")
    public ApiResponse<MaterialResponse> updateMaterial(
            @RequestBody MaterialDto request,
            @PathVariable int materialId
    ) {
        return ApiResponse.<MaterialResponse>builder()
                .result(materialService.updateMaterial(materialId,request))
                .build();
    }

    @Operation(summary = "Xóa danh mục theo ID", description = "Xóa danh mục theo ID được cung cấp")
    @DeleteMapping("/category/{categoryId}")
    public ApiResponse<String> deleteCategory(@PathVariable int categoryId) {
        return ApiResponse.<String>builder()
                .result(categoryService.deleteCategory(categoryId))
                .build();
    }

    @Operation(summary = "Xóa màu sắc theo ID", description = "Xóa màu sắc theo ID được cung cấp")
    @DeleteMapping("/color/{colorId}")
    public ApiResponse<String> deleteColor(@PathVariable int colorId) {
        return ApiResponse.<String>builder()
                .message(colorService.deleteColor(colorId))
                .build();
    }

    @Operation(summary = "Xóa kích cỡ theo ID", description = "Xóa kích cỡ theo ID được cung cấp")
    @DeleteMapping("/size/{sizeId}")
    public ApiResponse<String> deleteSize(@PathVariable int sizeId) {
        return ApiResponse.<String>builder()
                .message(sizeService.deleteSize(sizeId))
                .build();
    }

    @Operation(summary = "Xóa chất liệu theo ID", description = "Xóa chất liệu theo ID được cung cấp")
    @DeleteMapping("/material/{materialId}")
    public ApiResponse<String> deleteMaterial(@PathVariable int materialId) {
        return ApiResponse.<String>builder()
                .message(materialService.deleteMaterial(materialId))
                .build();
    }
}
