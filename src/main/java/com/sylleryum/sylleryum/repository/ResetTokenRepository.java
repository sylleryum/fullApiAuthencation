package com.sylleryum.sylleryum.repository;

import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.ResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends CrudRepository<ResetToken, Long> {

    Optional<ResetToken> findByApiUserUsername(String user);
    Optional<ResetToken>  findByResetToken(String token);
}
