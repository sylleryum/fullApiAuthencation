package com.sylleryum.sylleryum.email;

import org.springframework.scheduling.annotation.Async;

public interface EmailService {

    String LINK = "http://localhost:8080/v1/";
    String LINK_CONFIRM = LINK+"confirm?confirm-token=";
    String LINK_RESET = LINK+"reset?confirm=";
    String SENDER = "email@email.com";

    @Async
    void confirmEmail(String emailAddress, String firstName, String confirmationToken);
    @Async
    void resetEmail(String emailAddress, String firstName, String resetToken);
}
