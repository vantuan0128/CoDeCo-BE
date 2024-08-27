package com.prj.furni_shop.modules.authentication.dto.request;

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
public class RegisterRequest {
    @Email(message = "INVALID_INPUT_DATA")
    String email;

    @Size(min = 6, message = "INVALID_INPUT_DATA")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$", message = "INVALID_INPUT_DATA")
    String password;

    @Pattern(regexp = "^\\d{10}$", message = "INVALID_INPUT_DATA")
    String phoneNumber;

    String firstName;

    String lastName;

}
