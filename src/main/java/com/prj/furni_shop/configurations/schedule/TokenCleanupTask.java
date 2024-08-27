package com.prj.furni_shop.configurations.schedule;

import com.nimbusds.jwt.JWTClaimsSet;
import com.prj.furni_shop.modules.authentication.entity.Token;
import com.prj.furni_shop.modules.authentication.repository.TokenRepository;
import com.prj.furni_shop.utils.JWTTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class TokenCleanupTask {
    @Autowired
    private TokenRepository tokenRepository;

    @Value("${jwt.signerKey}")
    private String signerKey;

    @Scheduled(cron = "0 0 */12 * * *")
    public void cleanUpExpiredTokens() {
        List<Token> tokens = tokenRepository.findAll();

        List<Token> expiredTokens = new ArrayList<>();

        Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            JWTClaimsSet claimsSet = JWTTokenUtil.parseToken(token.getToken(), signerKey);

            if (claimsSet != null && claimsSet.getExpirationTime().before(Date.from(Instant.now()))) {
                expiredTokens.add(token);
                iterator.remove();
            }
        }
        if (!expiredTokens.isEmpty()) {
            tokenRepository.deleteAll(expiredTokens);
        }
    }
}
