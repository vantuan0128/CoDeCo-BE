package com.prj.furni_shop.modules.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailResponse {
    int productDetailId;

    int price;

    int quantity;

    int productId;

    String sizeName;

    String colorName;

    String materialName;
}
