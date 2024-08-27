package com.prj.furni_shop.modules.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDto {
    String nameReceiver;

    @Pattern(regexp = "^\\d{10}$", message = "INVALID_INPUT_DATA")
    String phoneReceiver;

    String nation;

    String province;

    String district;

    String detail;

    int addressType;

    int userId;

    int isDefault = 1;
}
