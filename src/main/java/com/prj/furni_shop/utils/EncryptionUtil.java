package com.prj.furni_shop.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class EncryptionUtil {
    public String encodeBase64(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public String decodeBase64(String encodedData) {
        return new String(Base64.getDecoder().decode(encodedData));
    }
}
