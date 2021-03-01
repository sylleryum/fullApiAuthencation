package com.sylleryum.sylleryum.repository;

import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String token);
    Optional<RefreshToken> findByApiUser(ApiUser user);

}
