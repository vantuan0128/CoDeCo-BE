package com.prj.furni_shop.modules.category.mapper;

import com.prj.furni_shop.modules.category.dto.request.SizeDto;
import com.prj.furni_shop.modules.category.dto.response.SizeResponse;
import com.prj.furni_shop.modules.category.entity.Size;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SizeMapper {
    @Mapping(target = "sizeId", ignore = true)
    Size toSize(SizeDto request);

    SizeResponse toSizeResponse(Size request);

    @Mapping(target = "sizeId", ignore = true)
    void updateSize(@MappingTarget Size size, SizeDto request);
}
