package com.prj.furni_shop.modules.order.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String note;

    int paymentMethod;

    int addressId;

    Integer voucherId;
}
