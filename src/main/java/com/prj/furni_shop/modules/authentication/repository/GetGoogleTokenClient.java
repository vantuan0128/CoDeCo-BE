package com.prj.furni_shop.modules.authentication.repository;

import com.prj.furni_shop.modules.authentication.dto.request.ExchangeTokenRequest;
import com.prj.furni_shop.modules.authentication.dto.respone.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "getGoogleTokenClient", url = "https://oauth2.googleapis.com")
public interface GetGoogleTokenClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@RequestBody ExchangeTokenRequest request);
}
