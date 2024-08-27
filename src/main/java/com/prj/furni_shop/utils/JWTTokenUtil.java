package com.prj.furni_shop.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JWTTokenUtil {
    public static JWTClaimsSet parseToken(String token, String signingKey) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(signingKey.getBytes());

            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet();
            } else {
                return null;
            }
        } catch (ParseException | JOSEException e) {
            e.printStackTrace();
            return null;
        }
    }
}
