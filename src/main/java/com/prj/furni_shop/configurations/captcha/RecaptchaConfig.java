package com.prj.furni_shop.configurations.captcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecaptchaConfig {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    @Value("${recaptcha.verify.url}")
    private String verifyUrl;

    public String getSecretKey() {
        return secretKey;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }
}