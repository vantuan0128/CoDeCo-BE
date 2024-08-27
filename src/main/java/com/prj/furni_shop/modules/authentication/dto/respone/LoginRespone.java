package com.prj.furni_shop.modules.authentication.dto.respone;

import com.prj.furni_shop.modules.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRespone {

    String accessToken;

    String refreshToken;

    UserResponse userResponse;
}
