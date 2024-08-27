package com.prj.furni_shop.modules.product.controller;

import com.prj.furni_shop.base.ApiResponse;
import com.prj.furni_shop.common.PaginationWrapper;
import com.prj.furni_shop.modules.product.dto.request.PagingDto;
import com.prj.furni_shop.modules.product.dto.request.ProductDetailFilterDto;
import com.prj.furni_shop.modules.product.dto.request.ProductFilterDto;
import com.prj.furni_shop.modules.product.dto.response.AvailableAttributesResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductDetailInfoResponse;
import com.prj.furni_shop.modules.product.dto.response.ProductResponse;
import com.prj.furni_shop.modules.product.service.UserProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Sản phẩm", description = "APIs liên quan đến sản phẩm")
public class UserProductController {

    UserProductService userProductService;

    @Operation(summary = "Lấy tất cả sản phẩm", description = "Lấy danh sách tất cả sản phẩm (Không bao gồm sản phẩm bị vô hiệu hóa)")
    @GetMapping()
    public ApiResponse<PaginationWrapper<ProductResponse>> getActiveProductsForUser(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        return ApiResponse.<PaginationWrapper<ProductResponse>>builder()
                .result(userProductService.getActiveProductsForUser(page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Lấy tất cả sản phẩm theo danh mục sản phẩm", description = "Lấy tất cả sản phẩm theo danh mục (Không bao gồm sản phẩm bị vô hiệu hóa)")
    @GetMapping("/category/{categoryId}")
    public ApiResponse<PaginationWrapper<ProductResponse>> getActiveProductsForUserByCategoryId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @PathVariable int categoryId
    ){
        return ApiResponse.<PaginationWrapper<ProductResponse>>builder()
                .result(userProductService.getActiveProductsForUserByCategoryId(categoryId, page, pageSize, sortBy, direction))
                .build();
    }

    @Operation(summary = "Bộ lọc tìm kiếm", description = "Tìm các chi tiết sản phẩm liên quan dựa trên thông tin được cung cấp")
    @GetMapping("/search")
    public ApiResponse<PaginationWrapper<ProductResponse>> filterProducts(
            @RequestParam(required = false) List<Integer> categoryIds,
            @RequestParam(required = false) List<Integer> sizeIds,
            @RequestParam(required = false) List<Integer> colorIds,
            @RequestParam(required = false) List<Integer> materialIds,
            @RequestParam(required = false) Double fromPrice,
            @RequestParam(required = false) Double toPrice,
            @RequestParam(required = false) Boolean newest,
            @RequestParam(required = false) Boolean bestSeller,
            @RequestParam(required = false) String priceSort,
            @RequestParam(required = false) String searchValue,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        ProductFilterDto filter = new ProductFilterDto();
        filter.setCategoryIds(categoryIds);
        filter.setMaterialIds(materialIds);
        filter.setSizeIds(sizeIds);
        filter.setColorIds(colorIds);
        filter.setFromPrice(fromPrice);
        filter.setToPrice(toPrice);
        filter.setNewest(newest);
        filter.setBestSeller(bestSeller);
        filter.setPriceSort(priceSort);
        filter.setSearchValue(searchValue);

        PagingDto paging = new PagingDto();
        paging.setPage(page);
        paging.setPageSize(pageSize);

        return ApiResponse.<PaginationWrapper<ProductResponse>>builder()
                .result(userProductService.filterProducts(filter, paging))
                .build();
    }


    @Operation(summary = "Lấy thông tin sản phẩm", description = "Lấy thông tin sản phẩm từ mã sản phẩm được cung cấp cùng các chi tiết sản phẩm từ sản phẩm đó")
    @GetMapping("/{productId}")
    public ApiResponse<AvailableAttributesResponse> getProductDetailsByProductId(
            @PathVariable int productId
    ){
        return ApiResponse.<AvailableAttributesResponse>builder()
                .result(userProductService.getProductDetailsByProductId(productId))
                .build();
    }

    @Operation(summary = "Lấy tổng số sản phẩm theo các tiêu chí", description = "Lấy tổng số chi tiết sản phẩm theo các tiêu chí được cung cấp")
    @PostMapping("/productDetails/filter")
    public ApiResponse<ProductDetailInfoResponse> filterProductDetails(
            @RequestBody ProductDetailFilterDto request
    ) {
        return ApiResponse.<ProductDetailInfoResponse>builder()
                .result(userProductService.getTotalQuantityAndPrice(request))
                .build();
    }
}
