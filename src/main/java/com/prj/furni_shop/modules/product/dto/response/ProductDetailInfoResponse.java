package com.prj.furni_shop.modules.product.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetailInfoResponse {

    Integer productDetailId;

    int totalQuantity;

    Integer price;
}
