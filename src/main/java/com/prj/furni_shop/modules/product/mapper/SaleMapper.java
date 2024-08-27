package com.prj.furni_shop.modules.product.mapper;

import com.prj.furni_shop.modules.product.dto.request.SaleDto;
import com.prj.furni_shop.modules.product.dto.response.SaleResponse;
import com.prj.furni_shop.modules.product.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SaleMapper {

    Sale toSale(SaleDto request);

    SaleResponse toSaleResponse(Sale sale);

    void updateSale(@MappingTarget Sale sale, SaleDto request);
}
