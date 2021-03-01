package com.sylleryum.sylleryum.commons;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Collections;
import java.util.Map;

/**
 * class with all of the literals used on {@link com.sylleryum.sylleryum.controller.AuthController}
 */
public class ControllerReturnStrings {

    public static final String CONFIRM_ENABLE_USER = ResponseBodyBuilder.buildSingle("Message","Account activated! please login");
    public static final String CONFIRM_NEW_ACTIVATION_EMAIL = ResponseBodyBuilder.buildSingle("message",
            "if email isn't received, please email alisson_piuco@yahoo.com.br (BoostMyVocabulary uses a free transactional email " +
                    "and we cannot ensure its reliability)");
    public static final String CONFIRM_EXCEPTION = "Please add header key = confirm-token to confirm a user's registration with the email received " +
            "or header key = request-token, value = email to request a new confirmation email, ONE OF THESE ONLY, NOT BOTH";

    public static final String REFRESH_EXCEPTION = "Please add a refresh token in the header with key = refresh-token";

    public static final String RESET_NEW_TOKEN = ResponseBodyBuilder.buildSingle("message",
            "if a valid email address has been provided you should receive a link to reset the password. If an email isn't received, " +
                    "please email alisson_piuco@yahoo.com.br (BoostMyVocabulary uses a free transactional email " +
                    "and we cannot ensure its reliability)");
    public static final String RESET_ENABLE_TOKEN(String resetToken) throws JsonProcessingException {
        Map<String,String> response = Map.of(
                "token", resetToken,
                "message","reset token enabled, please send a post request to this endpoint with keys = token:below token and password:new pwd"
        );
        return ResponseBodyBuilder.build(response);
    }
    public static final String RESET_CONFIRM_PWD_CHANGE = ResponseBodyBuilder.buildSingle("message", "password changed successfully");
    public static final String RESET_EXCEPTION = "Please add header key = email to request a new password reset to provided email " +
            "or /reset?confirm={your-reset-token-received} to validate the received password reset token, ONE OF THESE ONLY, NOT BOTH";
}
