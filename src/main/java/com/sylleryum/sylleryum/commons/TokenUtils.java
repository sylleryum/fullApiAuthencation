package com.sylleryum.sylleryum.commons;

import com.sylleryum.sylleryum.config.security.JWTConfig;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

public class TokenUtils {

    private static final LocalDate CURRENT_TIME = LocalDate.now();
    public static final LocalDate REFRESH_VALIDITY = CURRENT_TIME.plusDays(7);
    public static final LocalDate ACCESS_VALIDITY = CURRENT_TIME.plusDays(1);
    public static final LocalDateTime RESET_VALIDITY = LocalDateTime.now().plusHours(2);

    public static final String USER = "USER";
    public static final Map<String, String> USER_ROLE = Collections.singletonMap("role", "ROLE_USER");

}
