package com.prj.furni_shop.modules.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateDto {
    @Pattern(regexp = "^\\d{10}$", message = "INVALID_INPUT_DATA")
    String phoneNumber;

    String firstName;

    String lastName;

    String avatarUrl;
}
