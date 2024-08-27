package com.prj.furni_shop.modules.category.mapper;

import com.prj.furni_shop.modules.category.dto.request.MaterialDto;
import com.prj.furni_shop.modules.category.dto.response.MaterialResponse;
import com.prj.furni_shop.modules.category.entity.Material;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaterialMapper {
    @Mapping(target = "materialId", ignore = true)
    Material toMaterial(MaterialDto request);

    MaterialResponse toMaterialResponse(Material request);

    @Mapping(target = "materialId", ignore = true)
    void updateMaterial(@MappingTarget Material material, MaterialDto request);
}
