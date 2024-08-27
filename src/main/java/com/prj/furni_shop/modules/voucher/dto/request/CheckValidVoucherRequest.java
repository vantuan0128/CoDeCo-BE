package com.prj.furni_shop.modules.voucher.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckValidVoucherRequest {
    int voucherId;
    int totalMoney;
}
