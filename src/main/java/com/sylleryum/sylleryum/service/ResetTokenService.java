package com.sylleryum.sylleryum.service;

import com.sylleryum.sylleryum.commons.TokenUtils;
import com.sylleryum.sylleryum.email.EmailService;
import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.ResetToken;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.exception.TokenException;
import com.sylleryum.sylleryum.repository.ResetTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {

    private final ResetTokenRepository resetTokenRepository;
    private final EmailService emailService;

    private static Logger logger = LoggerFactory.getLogger(ResetTokenService.class);

    @Autowired
    public ResetTokenService(ResetTokenRepository resetTokenRepository, EmailService emailService) {
        this.resetTokenRepository = resetTokenRepository;
        this.emailService = emailService;
    }

    public ResetToken findByApiUserEmail(String userEmail, String traceId) throws ResourceNotFoundException {

        Optional<ResetToken> resetToken = resetTokenRepository.findByApiUserUsername(userEmail);
        if (resetToken.isPresent()) {
            return resetToken.get();
        }
        throw new ResourceNotFoundException(traceId, "ResetToken not found");
    }

    public ResetToken findByResetToken(String token, String traceId) throws ResourceNotFoundException {
        Optional<ResetToken> resetToken = resetTokenRepository.findByResetToken(token);
        if (resetToken.isPresent()) {
            return resetToken.get();
        }
        throw new ResourceNotFoundException(traceId, "ResetToken not found");
    }

    /**
     * creates a new reset token in the DB and sends it to the provided email
     * @param email
     * @param traceId
     * @return
     * @throws ResourceNotFoundException
     */
    public boolean requestResetToken(String email, String traceId) throws ResourceNotFoundException {
        ResetToken resetToken = newResetToken(email, traceId);
        emailService.resetEmail(email, resetToken.getApiUser().getFirstName(), resetToken.getResetToken());
        return true;
    }

    /**
     * Adds an entry in the DB for a new token to reset password
     * @param email
     * @param traceId
     * @return reset token saved in the DB
     * @throws ResourceNotFoundException
     */
    private ResetToken newResetToken(String email, String traceId) throws ResourceNotFoundException {
        Optional<ResetToken> resetTokenSaved = resetTokenRepository.findByApiUserUsername(email);
        ResetToken resetToken;
        if (resetTokenSaved.isPresent()) {
            logger.debug("refresh the reset token");
            resetTokenSaved.get().setEnabled(false);
            resetTokenSaved.get().setResetToken("RES"+UUID.randomUUID().toString());
            resetTokenSaved.get().setValidity(TokenUtils.RESET_VALIDITY);
            resetToken = save(resetTokenSaved.get());
            return resetToken;
        }
        logger.debug("new reset token");
        //ApiUser apiUser = apiUserService.findByEmail(email, traceId);
        ApiUser apiUser = findByApiUserEmail(email, traceId).getApiUser();

        ResetToken resetTokenToSave = new ResetToken(apiUser, TokenUtils.RESET_VALIDITY);

        return resetToken = save(resetTokenToSave);
    }

    public ResetToken save(ResetToken resetToken) {
        return resetTokenRepository.save(resetToken);
    }

    public Optional<ResetToken> findById(Long aLong) {
        return resetTokenRepository.findById(aLong);
    }

    public ResetToken enableResetToken(String resetTokenReceived, String traceId) throws ResourceNotFoundException, TokenException {
        ResetToken resetToken = findByResetToken(resetTokenReceived, traceId);
        if (resetToken.getValidity().isAfter(LocalDateTime.now())){
            resetToken.setEnabled(true);
            save(resetToken);
            logger.debug("reset token enabled");
            return resetToken;
        }
        throw new TokenException(traceId,"Reset Token has expired, please request a new one");
    }
}
