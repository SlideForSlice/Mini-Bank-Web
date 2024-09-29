package com.java.bank.security;

import com.java.bank.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;


@Component
public class JWTUtil {

    @Value("${spring.jwt.secret}")
    private String secret;

    public String generateToken(String username, int id) {
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(600000).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withClaim("id", id)
                .withIssuedAt(new Date())
                .withIssuer("admin")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret));
    }

    public String verifyToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("admin")
                .build();

        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();

    }

    public int extractUserId(String token) {

        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withSubject("User details")
                    .withIssuer("admin")
                    .build();

            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("id").asInt();
        } catch (JWTVerificationException e) {
            // Логирование или обработка ошибки верификации
            throw new IllegalArgumentException("Invalid token", e);
        }
    }

}

