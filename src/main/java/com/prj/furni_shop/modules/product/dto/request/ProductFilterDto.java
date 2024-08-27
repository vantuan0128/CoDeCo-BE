package com.prj.furni_shop.modules.product.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductFilterDto {
    List<Integer> categoryIds;

    List<Integer> sizeIds;

    List<Integer> colorIds;

    List<Integer> materialIds;

    Double fromPrice;

    Double toPrice;

    Boolean newest;

    Boolean bestSeller;

    String priceSort;

    String searchValue;
}
