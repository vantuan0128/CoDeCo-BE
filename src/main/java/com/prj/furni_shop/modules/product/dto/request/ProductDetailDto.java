package com.prj.furni_shop.modules.product.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailDto {
    int price;

    int quantity;

    int productId;

    int sizeId;

    int colorId;

    int materialId;
}
