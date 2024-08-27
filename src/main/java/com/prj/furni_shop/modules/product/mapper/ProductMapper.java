package com.prj.furni_shop.modules.product.mapper;

import com.prj.furni_shop.modules.product.dto.request.ProductDto;
import com.prj.furni_shop.modules.product.dto.response.ProductResponse;
import com.prj.furni_shop.modules.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    Product toProduct(ProductDto request);

    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(target = "productImages", ignore = true)
    ProductResponse toProductResponse(Product product);
}
