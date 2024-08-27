package com.prj.furni_shop.modules.user.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {

    int addressId;

    String nameReceiver;

    String phoneReceiver;

    String nation;

    String province;

    String district;

    String detail;

    int addressType;

    int userId;

    int isDefault = 1;
}
