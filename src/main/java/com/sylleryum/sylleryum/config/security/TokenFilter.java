package com.sylleryum.sylleryum.config.security;

import com.sylleryum.sylleryum.commons.TraceIdGenerator;
import com.sylleryum.sylleryum.exception.IlegalCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.sylleryum.sylleryum.commons.JsonConverter.convertObjectToJson;

/**
 * used to validate the authorization token which is needed for every request after login (login configuration is found on {@link JwtUsernameAndPasswordAuthenticationFilter})
 */
public class TokenFilter extends OncePerRequestFilter {

    private final JWTKey jwtKey;
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);



    @Autowired
    public TokenFilter(JWTKey jwtKey) {
        this.jwtKey = jwtKey;
    }

    //@lombok.SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Optional<String> authorizationHeader = Optional.ofNullable(request.getHeader(JWTConfig.AUTHORIZATION_HEADER));
        Optional<String> traceIdHeader = Optional.ofNullable(request.getHeader("traceId"));

        System.out.println();
        if (!authorizationHeader.isPresent() || !authorizationHeader.get().startsWith(JWTConfig.TOKEN_PREFIX)) {
            System.out.println();
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.get().replace(JWTConfig.TOKEN_PREFIX, "");
        try {
            String username = JWTConfig.decodeToken(token,jwtKey.getLoginSecretKey()).getSubject();
            String role = JWTConfig.decodeToken(token,jwtKey.getLoginSecretKey()).getClaim("role").asString();

            Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = new HashSet<>();
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);
            simpleGrantedAuthoritySet.add(simpleGrantedAuthority);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthoritySet
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println();
        }
        catch (Exception e) {
            String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);
            logger.error("TraceId: {}, JWT Token Not Valid", traceId, e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            String responseJson = convertObjectToJson(new IlegalCredentialsException(traceId, e.getMessage()));
            response.getWriter().write(responseJson);
            return;
        }

        filterChain.doFilter(request, response);
    }





}
