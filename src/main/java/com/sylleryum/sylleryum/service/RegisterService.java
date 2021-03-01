package com.sylleryum.sylleryum.service;

import com.sylleryum.sylleryum.commons.TokenUtils;
import com.sylleryum.sylleryum.email.EmailService;
import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.exception.ResourceAlreadyExistsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.config.security.JWTConfig;
import com.sylleryum.sylleryum.config.security.JWTKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class RegisterService {

    private final ApiUserService apiUserService;
    private final JWTKey jwtKey;
    private final EmailService emailService;

    @Autowired
    public RegisterService(ApiUserService apiUserService, JWTKey jwtKey, EmailService emailService) throws ResourceNotFoundException {
        this.apiUserService = apiUserService;
        this.jwtKey = jwtKey;
        this.emailService = emailService;
    }

    public boolean ActivateConfirmToken(String confirmToken, String traceId) throws ResourceNotFoundException, ResourceAlreadyExistsException {

        String username = JWTConfig.decodeToken(confirmToken, jwtKey.getConfirmationSecretKey()).getSubject();
        ApiUser user = apiUserService.findByEmail(username, traceId);
        if (!user.isEnabled()) {
            user.setEnabled(true);
            apiUserService.save(user, traceId);
            return true;
        }
        throw new ResourceAlreadyExistsException("User is already enabled, please use login option", traceId);
    }

    /**
     * gets a new confirmToken and sends to the email provided
     * @param email
     * @param traceId
     * @return firstName, token
     * @throws ResourceNotFoundException
     * @throws ResourceAlreadyExistsException
     */
    public boolean requestConfirmEmail(String email, String traceId) throws ResourceNotFoundException, ResourceAlreadyExistsException {

        Map<String, ApiUser> emailApiUser = newConfirmToken(email, traceId);

        Map.Entry<String, ApiUser> onlyEntry = emailApiUser.entrySet().iterator().next();
        String confirmationToken = onlyEntry.getKey();
        ApiUser apiUser = onlyEntry.getValue();

        emailService.confirmEmail(email, apiUser.getFirstName(), confirmationToken);
        return true;
    }

    /**
     *
     * @param email
     * @param traceId
     * @return the confirmation token and ApiUser of the passed email
     * @throws ResourceNotFoundException
     * @throws ResourceAlreadyExistsException
     */
    private Map<String, ApiUser> newConfirmToken(String email, String traceId) throws ResourceNotFoundException, ResourceAlreadyExistsException {
        ApiUser userToSendToken = apiUserService.findByEmail(email, traceId);
        if (!userToSendToken.isEnabled()) {
            String confirmationToken = JWTConfig.JWTTokenBuilder(userToSendToken.getUsername(),
                    TokenUtils.ACCESS_VALIDITY,
                    jwtKey.getConfirmationSecretKey());
            return Map.of(confirmationToken, userToSendToken);
        }
        throw new ResourceAlreadyExistsException("User is already enabled, please use login option", traceId);
    }

}
