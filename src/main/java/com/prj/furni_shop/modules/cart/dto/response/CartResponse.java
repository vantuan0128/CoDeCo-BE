package com.prj.furni_shop.modules.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {

    Integer cartId;

    Integer totalCount;

    Boolean selectedAll;

    Integer selectedCount;

    List<CartItemResponse> cartItemResponses;

    Double total;


}
