package com.sylleryum.sylleryum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sylleryum.sylleryum.commons.TokenUtils;
import com.sylleryum.sylleryum.commons.TraceIdGenerator;
import com.sylleryum.sylleryum.email.EmailService;
import com.sylleryum.sylleryum.email.EmailServiceImpl;
import com.sylleryum.sylleryum.entity.PasswordResetRequest;
import com.sylleryum.sylleryum.entity.RefreshToken;
import com.sylleryum.sylleryum.entity.ResetToken;
import com.sylleryum.sylleryum.entity.UserToRegister;
import com.sylleryum.sylleryum.exception.ResourceAlreadyExistsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.exception.TokenException;
import com.sylleryum.sylleryum.config.security.JWTKey;
import com.sylleryum.sylleryum.config.ResponseTokenBuilder;
import com.sylleryum.sylleryum.service.ApiUserService;
import com.sylleryum.sylleryum.service.RefreshTokenService;
import com.sylleryum.sylleryum.service.RegisterService;
import com.sylleryum.sylleryum.service.ResetTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static com.sylleryum.sylleryum.commons.ControllerReturnStrings.*;

/**
 * additional endpoint not included here is /login, which receives two params: username(email) and password. if login is successful, returns access and refresh token
 */
@RestController
@RequestMapping(path = "/v1")
public class AuthController {

    private final ApiUserService apiUserService;
    private final RefreshTokenService refreshTokenService;
    private final ResponseTokenBuilder responseTokenBuilder;
    private final RegisterService registerService;
    private final EmailService emailService;
    private final ResetTokenService resetTokenService;
    private final JWTKey jwtKey;
    private static Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    public AuthController(ApiUserService apiUserService, RefreshTokenService refreshTokenService, ResponseTokenBuilder responseTokenBuilder, RegisterService registerService, EmailServiceImpl emailService, ResetTokenService resetTokenService, JWTKey jwtKey) {
        this.apiUserService = apiUserService;
        this.refreshTokenService = refreshTokenService;
        this.responseTokenBuilder = responseTokenBuilder;
        this.registerService = registerService;
        this.emailService = emailService;
        this.resetTokenService = resetTokenService;
        this.jwtKey = jwtKey;
    }

    /**
     * Creates a new user
     * @param traceIdHeader
     * @param userToRegister
     * @return if successful, returns the user persisted, if not, returns the errors on the request
     * @throws ResourceAlreadyExistsException
     */
    @PostMapping("/register")
    ResponseEntity<UserToRegister> register(@RequestHeader(value = "trace-id") Optional<String> traceIdHeader,
                                            @Valid @RequestBody UserToRegister userToRegister,
                                            BindingResult bindingResult) throws ResourceAlreadyExistsException {
        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);
        System.out.println();

        if (bindingResult.hasErrors()) {
            UserToRegister userError = new UserToRegister(bindingResult);
            return new ResponseEntity<>(userError, HttpStatus.BAD_REQUEST);
        }

        UserToRegister savedUser = apiUserService.registerNewUser(userToRegister, traceId);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * used to enable user, either confirmToken or usernameEmail should be present, not both. By default it sends an email to the provided email (usernameEmail)
     * with the confirm token, to get a confirm token without sending email the method newConfirmToken from {@link RegisterService} should be used instead (currently private).
     * @param traceIdHeader
     * @param confirmToken enable user with the token received through the param confirm-token=token-received
     * @param usernameEmail requests a new confirm token to activate the user through the param request-token=email
     * @return
     * @throws TokenException
     * @throws ResourceNotFoundException
     * @throws ResourceAlreadyExistsException
     */
    @GetMapping(path = "/confirm", produces = "application/json")
    ResponseEntity<String> confirm(@RequestHeader(value = "trace-id") Optional<String> traceIdHeader,
                              @RequestParam("confirm-token") Optional<String> confirmToken,
                              @RequestHeader("request-token") Optional<String> usernameEmail) throws TokenException, ResourceNotFoundException, ResourceAlreadyExistsException {

        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);

        if (confirmToken.isPresent() && usernameEmail.isPresent()){
            throw new TokenException(traceId, CONFIRM_EXCEPTION);
        }

        //enable user
        if (confirmToken.isPresent()) {
            registerService.ActivateConfirmToken(confirmToken.get(), traceId);
            return ResponseEntity.ok(CONFIRM_ENABLE_USER);
        }
        //new activation email
        if (usernameEmail.isPresent()) {
            String email = usernameEmail.get().trim();
            registerService.requestConfirmEmail(email, traceId);
            return ResponseEntity.ok(CONFIRM_NEW_ACTIVATION_EMAIL);
        }
        throw new TokenException(traceId, CONFIRM_EXCEPTION);
    }

    /**
     * used to request a new access token by providing a refresh token (which is received when logging in on endpoint /login)
     * @param traceIdHeader
     * @param refeshTokenHeader
     * @return on success, a new access token and refresh token
     * @throws TokenException
     * @throws JsonProcessingException
     * @throws ResourceNotFoundException
     */
    @GetMapping(path = "/refresh", produces = "application/json")
    ResponseEntity<String> refreshToken(@RequestHeader("trace-id") Optional<String> traceIdHeader,
                                        @RequestHeader("refresh-token") Optional<String> refeshTokenHeader) throws TokenException, JsonProcessingException, ResourceNotFoundException {

        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);

        if (refeshTokenHeader.isPresent()) {
            RefreshToken refreshToken = refreshTokenService.ValidateRefreshToken(refeshTokenHeader, traceId);
            //send a new token
            String newToken = responseTokenBuilder.buildAuthTokenJsonResponse(
                    refreshToken.getApiUser().getUsername(),
                    TokenUtils.USER_ROLE);

            return ResponseEntity.ok(newToken);
        }

        //no token
        throw new TokenException(traceId, REFRESH_EXCEPTION);
    }

    /**
     * used to reset an user's password, this endpoint should be used prior to ResetPWD to validate and enable the password reset. By default it sends
     * a reset token to the provided email on the header email. To get a reset token without sending email the method newResetToken from {@link ResetTokenService} should be used instead (currently private).
     * @param traceIdHeader
     * @param email if email received in this parameter is valid, sends an email to confirm/enable the password reset
     * @param resetToken receives the confirm-token and enables the same, if this is successful, ResetPWD endpoint can be used
     * @return
     * @throws TokenException
     * @throws ResourceNotFoundException
     * @throws JsonProcessingException
     */
    @GetMapping(path = "/reset", produces = "application/json")
    ResponseEntity<String> requestResetPWD(@RequestHeader("trace-id") Optional<String> traceIdHeader,
                                           @RequestHeader("email") Optional<String> email,
                                           @RequestParam("confirm") Optional<String> resetToken) throws TokenException, ResourceNotFoundException, JsonProcessingException {
        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);

        if (resetToken.isPresent() && email.isPresent()){
            throw new TokenException(traceId, RESET_EXCEPTION);
        }

        //enable token
        if (resetToken.isPresent()){
            ResetToken resetTokenToReturn = resetTokenService.enableResetToken(resetToken.get(), traceId);

            return ResponseEntity.ok(RESET_ENABLE_TOKEN(resetTokenToReturn.getResetToken()));
        }

        //new token
        if (email.isPresent()) {
            try {
//            Optional<ResetToken> byApiUserEmail = resetTokenService.findByApiUserEmail(email.get(), traceId);
                resetTokenService.requestResetToken(email.get(), traceId);
                return ResponseEntity.ok(RESET_NEW_TOKEN);

                //invalid email
            } catch (Exception e) {
                System.out.println();
                logger.error(traceId + " - " + e.getMessage());
                return ResponseEntity.ok(RESET_NEW_TOKEN);
            }
        }
        throw new TokenException(traceId, RESET_EXCEPTION);

    }

    /**
     * used after validating the confirm-token on endpoint requestResetPWD to reset user's password
     * @param traceIdHeader
     * @param passwordResetRequestReceived
     * @return
     * @throws ResourceNotFoundException
     * @throws TokenException
     */
    @PostMapping(path = "/reset", produces = "application/json")
    ResponseEntity<?> ResetPWD(@RequestHeader("trace-id") Optional<String> traceIdHeader,
                                                  @Valid @RequestBody PasswordResetRequest passwordResetRequestReceived,
                                                  BindingResult bindingResult) throws ResourceNotFoundException, TokenException {
        String traceId = TraceIdGenerator.generateTraceId(traceIdHeader);


        if (bindingResult.hasErrors()) {
            PasswordResetRequest passwordResetRequest = new PasswordResetRequest(bindingResult);
            return new ResponseEntity<>(passwordResetRequest, HttpStatus.BAD_REQUEST);
        }
        apiUserService.changePasswordFromResetToken(passwordResetRequestReceived, traceId);

        return ResponseEntity.ok(RESET_CONFIRM_PWD_CHANGE);
    }


}
