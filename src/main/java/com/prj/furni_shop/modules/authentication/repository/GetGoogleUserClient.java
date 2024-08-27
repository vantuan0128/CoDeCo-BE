package com.prj.furni_shop.modules.authentication.repository;


import com.prj.furni_shop.modules.authentication.dto.respone.ExchangeGoogleUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "getGoogleUserClient", url = "https://www.googleapis.com/oauth2/v1")
public interface GetGoogleUserClient {
    @GetMapping("/userinfo")
    ExchangeGoogleUserResponse getUserInfo(@RequestHeader("Authorization") String authorization,
                                           @RequestParam("access_token") String accessToken,
                                           @RequestParam("alt") String alt);
}
