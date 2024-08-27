package com.prj.furni_shop.modules.order.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prj.furni_shop.modules.order.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    int orderId;

    int userId;

    String orderAddress;

    String nameReceiver;

    String phoneReceiver;

    String addressType;

    OrderStatus status;

    String note;

    int totalMoney;

    Double discountedPrice;

    int count;

    String paymentMethod;

    String isPaid;

    List<OrderItemResponse> orderItems;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime createdAt;

}
