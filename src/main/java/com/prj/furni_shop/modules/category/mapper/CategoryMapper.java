package com.prj.furni_shop.modules.category.mapper;

import com.prj.furni_shop.modules.category.dto.request.CategoryDto;
import com.prj.furni_shop.modules.category.dto.response.CategoryResponse;
import com.prj.furni_shop.modules.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "categoryId", ignore = true)
    Category toCategory(CategoryDto request);

    CategoryResponse toCategoryResponse(Category request);

    @Mapping(target = "categoryId", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryDto request);
}
