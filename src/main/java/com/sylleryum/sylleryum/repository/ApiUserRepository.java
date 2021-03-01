package com.sylleryum.sylleryum.repository;

import com.sylleryum.sylleryum.entity.ApiUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiUserRepository extends CrudRepository<ApiUser, Long> {

    Optional<ApiUser> findByUsername(String username);
}
