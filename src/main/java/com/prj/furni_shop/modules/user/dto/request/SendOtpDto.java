package com.prj.furni_shop.modules.user.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SendOtpDto {
    @Email(message = "INVALID_INPUT_DATA")
    String email;
}
