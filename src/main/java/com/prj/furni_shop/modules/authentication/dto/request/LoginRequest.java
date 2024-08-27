package com.prj.furni_shop.modules.authentication.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @Email(message = "INVALID_INPUT_DATA")
    String email;
    String password;
    String recaptchaToken;
}
