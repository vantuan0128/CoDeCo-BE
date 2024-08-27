package com.prj.furni_shop.modules.cart.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    int cartItemId;

    int productDetailId;

    String image;

    String productName;

    String colorName;

    String materialName;

    String sizeName;

    int price;

    int count;

    Double percent;

    Boolean selected;

}
