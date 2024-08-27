package com.prj.furni_shop.modules.category.mapper;

import com.prj.furni_shop.modules.category.dto.request.ColorDto;
import com.prj.furni_shop.modules.category.dto.response.ColorResponse;
import com.prj.furni_shop.modules.category.entity.Color;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ColorMapper {
    @Mapping(target = "colorId", ignore = true)
    Color toColor(ColorDto request);

    ColorResponse toColorResponse(Color request);

    @Mapping(target = "colorId", ignore = true)
    void updateColor(@MappingTarget Color color, ColorDto request);
}
