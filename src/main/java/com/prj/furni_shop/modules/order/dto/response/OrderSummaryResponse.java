package com.prj.furni_shop.modules.order.dto.response;

import com.prj.furni_shop.modules.order.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderSummaryResponse {

    int orderId;

    OrderStatus status;

    int totalMoney;

    String productName;

    String imageUrl;

    int count;
}
