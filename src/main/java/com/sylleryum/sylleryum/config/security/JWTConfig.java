package com.sylleryum.sylleryum.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.time.LocalDate;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTConfig {


//    public static final java.sql.Date EXPIRE_USER = java.sql.Date.valueOf(CURRENT_TIME.now().plusDays(1));
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";


    /**
     *
     * @param subject
     * @param expiration
     * @param key secret key
     * @param customclaims if none, pass null
     * @return
     */
    public static String JWTTokenBuilder(String subject, LocalDate expiration, String key, Map<String, String> customclaims) {

        JWTCreator.Builder builder = JWT.create()
                .withSubject(subject)
                .withExpiresAt(java.sql.Date.valueOf(expiration));
        if (customclaims!= null) {
            for (Map.Entry<String, String> entry : customclaims.entrySet()) {
                builder.withClaim(entry.getKey(), entry.getValue());
            }
        }
        String token = builder.sign(HMAC512(key));


        return token;
    }

    public static String JWTTokenBuilder(String subject, LocalDate expiration, String key) {
        return JWTTokenBuilder(subject, expiration, key, null);
    }

    public static DecodedJWT decodeToken(String token, String key) {
        return JWT.require(HMAC512(key))
                .build()
                .verify(token);
    }

    //expired token for test:
    //eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZTMiLCJyb2xlIjoiUk9MRV9BRE1JTiIsImV4cCI6MTYwMTY5NDAwMH0.RTrsDsskU0r-nsI1Da79xuB_NdkDON3FyFhn8VmNbjl0JDWj1aDzQKv7FqWcWicBMIxLi4alXjm4amTb1lkPqQ
}
