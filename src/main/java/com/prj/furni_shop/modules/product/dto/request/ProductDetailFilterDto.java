package com.prj.furni_shop.modules.product.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailFilterDto {
    int productId;

    Integer colorId;

    Integer sizeId;

    Integer materialId;
}
