package com.sylleryum.sylleryum.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

//@PropertySource("classpath:application.properties")
@Configuration
public class JWTKey {

    @Value("${application.jwt.LoginSecretKey}")
    private String loginSecretKey;
    @Value("${application.jwt.confirmationSecretKey}")
    private String confirmationSecretKey;


    public String getLoginSecretKey() {
        return loginSecretKey;
    }
    public String getConfirmationSecretKey() {
        return loginSecretKey;
    }
}
