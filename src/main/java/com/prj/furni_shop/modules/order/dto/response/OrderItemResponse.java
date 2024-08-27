package com.prj.furni_shop.modules.order.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    int orderItemId;

    String name;

    String imageUrl;

    String size;

    String color;

    String material;

    int price;

    int count;
}
