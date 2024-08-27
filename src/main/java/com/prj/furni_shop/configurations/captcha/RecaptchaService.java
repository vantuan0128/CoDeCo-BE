package com.prj.furni_shop.configurations.captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class RecaptchaService {

    @Autowired
    private RecaptchaConfig recaptchaConfig;

    public boolean verifyRecaptcha(String token) {
        RestTemplate restTemplate = new RestTemplate();
        String verifyUrl = UriComponentsBuilder.fromHttpUrl(recaptchaConfig.getVerifyUrl())
                .queryParam("secret", recaptchaConfig.getSecretKey())
                .queryParam("response", token)
                .toUriString();

        // Debug log
//        System.out.println("Verification URL: " + verifyUrl);

        Map<String, Object> response = restTemplate.postForObject(verifyUrl, null, Map.class);

        // Debug log
        System.out.println("reCAPTCHA API Response: " + response);

        return (Boolean) response.get("success");
    }
}
