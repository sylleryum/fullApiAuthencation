package com.sylleryum.sylleryum.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sylleryum.sylleryum.commons.TraceIdGenerator;
import com.sylleryum.sylleryum.config.ResponseTokenBuilder;
import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.exception.IlegalCredentialsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.sylleryum.sylleryum.commons.JsonConverter.convertObjectToJson;

/**
 * used to login and therefore receive an access token needed to every request (access token is validated on {@link TokenFilter})
 */
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    static class InnerReadUsernamePassword {
        private String username;
        private String password;

        public InnerReadUsernamePassword() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    private final AuthenticationManager authenticationManager;
    private final ResponseTokenBuilder responseTokenBuilder;
    private static final Logger logger = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthenticationFilter.class);

    @Autowired
    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, ResponseTokenBuilder responseTokenBuilder) {
        this.authenticationManager = authenticationManager;
        this.responseTokenBuilder = responseTokenBuilder;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Optional<String> traceIdHeader = Optional.ofNullable(request.getHeader("traceId"));

        InnerReadUsernamePassword authenticationRequest = null;
        try {
            authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), InnerReadUsernamePassword.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword());

            Authentication authenticate = authenticationManager.authenticate(authentication);
            System.out.println();
            return authenticate;
        } catch (IOException e) {
            logger.error("Authentication failed for Username: {} ", authenticationRequest.getUsername(), e.getMessage());

//            String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);
//            logger.error("TraceId: {}, Credentials Not Valid", traceId, e);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
//            response.setContentType("application/json");
//            String responseJson = null;
//            try {
//                responseJson = convertObjectToJson(new IlegalTokenException(traceId, e.getMessage()));
//                response.getWriter().write(responseJson);
//            } catch (IOException io) {
//                logger.error("error writing Json", traceId, io);
//                throw new RuntimeException(e);
//
//            }
//            System.out.println();
            throw new RuntimeException(e);
        }

        //return super.attemptAuthentication(request, response);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        ApiUser authenticatedUser = (ApiUser) authResult.getPrincipal();


//        String token = JWT.create()
//                .withSubject(authResult.getName())
//                .withExpiresAt(expiration)
//                .withClaim("role", authenticatedUser.getRole())
//                .sign(HMAC512(jwtKey.getSecretKey()));

        Map<String, String> claims = new HashMap<String, String>();
        claims.put("role", authenticatedUser.getRole());

        String token = null;
        try {
            token = responseTokenBuilder.buildAuthTokenJsonResponse(authResult.getName(), claims);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        }

        response.getWriter().write(token);
        response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.addHeader(JWTConfig.AUTHORIZATION_HEADER, JWTConfig.TOKEN_PREFIX + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        Optional<String> traceIdHeader = Optional.ofNullable(request.getHeader("traceId"));
        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);

        try {
            logger.error("TraceId: {}, Access denied", traceId, failed.fillInStackTrace());
            System.out.println();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            String responseJson;
            if (failed.getClass() == DisabledException.class) {
                responseJson = convertObjectToJson(new IlegalCredentialsException(traceId, "this account is not enabled. Please send a get request to endpoint /confirm with header key = request-token with your email"));
            } else {
                responseJson = convertObjectToJson(new IlegalCredentialsException(traceId, failed.getLocalizedMessage()));
            }

            response.getWriter().write(responseJson);
            return;
        } catch (Exception e) {
            logger.error("TraceId: {}, Other error", traceId, failed.fillInStackTrace());
        }

    }
}
