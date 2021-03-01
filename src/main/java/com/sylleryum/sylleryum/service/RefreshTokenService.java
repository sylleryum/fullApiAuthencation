package com.sylleryum.sylleryum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.RefreshToken;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.exception.TokenException;
import com.sylleryum.sylleryum.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final ApiUserService apiUserService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenService(ApiUserService apiUserService, RefreshTokenRepository refreshTokenRepository) {
        this.apiUserService = apiUserService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        //TODO throw exception
        return refreshTokenRepository.findByRefreshToken(token);
    }

    private Optional<RefreshToken> findByApiUser(ApiUser apiUserToSearch){
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByApiUser(apiUserToSearch);
        return refreshToken;
    }

    public RefreshToken ValidateRefreshToken(Optional<String> refreshTokenToFind, String traceId) throws TokenException, ResourceNotFoundException, JsonProcessingException {
        Optional<RefreshToken> refreshToken = findByToken(refreshTokenToFind.get());

        if (!refreshToken.isPresent()) {
            //token not found
            throw new TokenException(traceId, "refresh token not valid");
        }
        LocalDate validity = LocalDate.parse(refreshToken.get().getValidity());

        if (!validity.isAfter(LocalDate.now())) {
            //token expired
            throw new TokenException(traceId, "refresh token has expired, please login again with username and password at endpoint: /login");
        }

        return refreshToken.get();
    }

    public RefreshToken save(RefreshToken refreshToken) {

        Optional<RefreshToken> refreshTokenFromApiUser = findByApiUser(refreshToken.getApiUser());
        if (!refreshTokenFromApiUser.isPresent()){
            return refreshTokenRepository.save(refreshToken);
        }

        refreshTokenFromApiUser.get().setRefreshToken("REF"+ UUID.randomUUID().toString());
        refreshTokenFromApiUser.get().setValidity(LocalDate.parse(refreshToken.getValidity()));
        return refreshTokenRepository.save(refreshTokenFromApiUser.get());
    }

}
