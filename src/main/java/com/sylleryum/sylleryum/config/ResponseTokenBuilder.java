package com.sylleryum.sylleryum.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.sylleryum.commons.TokenUtils;
import com.sylleryum.sylleryum.commons.TraceIdGenerator;
import com.sylleryum.sylleryum.config.security.JWTConfig;
import com.sylleryum.sylleryum.config.security.JWTKey;
import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.RefreshToken;
import com.sylleryum.sylleryum.entity.ResponseToken;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.service.ApiUserService;
import com.sylleryum.sylleryum.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.Map;
import java.util.Optional;

@Component
public class ResponseTokenBuilder {

    RefreshTokenService refreshTokenService;
    ApiUserService apiUserService;
    private final JWTKey jwtKey;

    @Autowired
    public ResponseTokenBuilder(RefreshTokenService refreshTokenService, ApiUserService apiUserService, JWTKey jwtKey) {
        this.refreshTokenService = refreshTokenService;
        this.apiUserService = apiUserService;
        this.jwtKey = jwtKey;
    }

    /**
     * Builds a {@link ResponseToken} (Access token + refresh token) which is returned after user logging in
     * @param username username from the authenticated user
     * @param customclaims
     * @return ResponseToken as Json
     * @throws JsonProcessingException
     */
    public String buildAuthTokenJsonResponse(String username, Map<String, String> customclaims) throws JsonProcessingException, ResourceNotFoundException {
        String traceId = TraceIdGenerator.generateTraceId(Optional.empty());
        //build access token
        String JWTAccessToken = JWTConfig.JWTTokenBuilder(username, TokenUtils.ACCESS_VALIDITY, jwtKey.getLoginSecretKey(), customclaims);

        //build refresh token
        ApiUser user = apiUserService.findByEmail(username, traceId);
        RefreshToken refreshToken = refreshTokenService.save(new RefreshToken(user, TokenUtils.REFRESH_VALIDITY));
        System.out.println();
        //assembling
        ResponseToken responseToken = new ResponseToken(JWTAccessToken, TokenUtils.ACCESS_VALIDITY, refreshToken);
        String json = new ObjectMapper().writeValueAsString(responseToken);
        return json;
    }

}
