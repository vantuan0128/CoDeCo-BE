package com.prj.furni_shop.modules.product.mapper;

import com.prj.furni_shop.modules.product.dto.request.ProductDetailDto;
import com.prj.furni_shop.modules.product.dto.response.ProductDetailResponse;
import com.prj.furni_shop.modules.product.entity.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductDetailMapper {
    ProductDetail toProductDetail(ProductDetailDto request);

    ProductDetailResponse toProductDetailResponse(ProductDetail productDetail);

    void updateProductDetail(@MappingTarget ProductDetail productDetail, ProductDetailDto request);
}
