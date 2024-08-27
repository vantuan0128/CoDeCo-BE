package com.prj.furni_shop.modules.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecoverPasswordDto {

    String otp;

    @Email(message = "INVALID_INPUT_DATA")
    String email;

    @Size(min = 6, message = "INVALID_INPUT_DATA")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$", message = "INVALID_INPUT_DATA")
    String newPassword;

    String confirmPassword;
}
