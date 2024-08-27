package com.prj.furni_shop.modules.category.dto.response;

import com.prj.furni_shop.modules.category.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryResponse {
    int categoryId;

    String name;

    List<SubCategoryResponse> subCategoriesInfo;
}
