package com.prj.furni_shop.modules.cart.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherInCartResponse {
    int userVoucherId;
    int voucherId;
    String code;
    Double discount;
    String description;
    Double minValue;
    Boolean canUse;
    Boolean selected;
}
