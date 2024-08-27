package com.prj.furni_shop.configurations.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.prj.furni_shop.exception.AppException;
import com.prj.furni_shop.exception.ErrorCode;
import com.prj.furni_shop.modules.user.entity.User;
import com.prj.furni_shop.modules.user.repository.UserRepository;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Component
public class JwtService {
    @NonFinal
    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.access-duration}")
    private int ACCESS_DURATION;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    private int REFRESH_DURATION;

    @Autowired
    private UserRepository userRepository;


    public String generateAccessToken(User user){
        return buildToken(user,ACCESS_DURATION);
    }

    public String generateRefreshToken(User user){
        return buildToken(user,REFRESH_DURATION);
    }

    public String buildToken(User user, int duration) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getUserId()))
                .issuer("abc.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .claim("email", user.getEmail())
                .claim("scope", user.getRole().name())
                .claim("uav", user.getAuthVersion())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }


    public boolean verifyToken(String token) {
        try {

            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            int uav = signedJWT.getJWTClaimsSet().getIntegerClaim("uav");
            int userId = Integer.parseInt(signedJWT.getJWTClaimsSet().getSubject());

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new AppException(ErrorCode.NOT_EXISTED));

            return signedJWT.verify(verifier) && expiryTime.after(new Date()) && (user.getAuthVersion() == uav);

        } catch (ParseException | JOSEException | AppException e) {
            return false;
        }
    }
}
