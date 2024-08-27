package com.prj.furni_shop.modules.authentication.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenRespone {
    private String accessToken;
    private String refreshToken;
}
