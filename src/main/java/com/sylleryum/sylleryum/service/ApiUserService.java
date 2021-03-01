package com.sylleryum.sylleryum.service;

import com.sylleryum.sylleryum.entity.ApiUser;
import com.sylleryum.sylleryum.entity.PasswordResetRequest;
import com.sylleryum.sylleryum.entity.ResetToken;
import com.sylleryum.sylleryum.entity.UserToRegister;
import com.sylleryum.sylleryum.exception.ResourceAlreadyExistsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.exception.TokenException;
import com.sylleryum.sylleryum.repository.ApiUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiUserService implements UserDetailsService {


    private final ApiUserRepository apiUserRepository;
    private final ResetTokenService resetTokenService;
    private final PasswordEncoder passwordEncoder;

    public ApiUserService(ApiUserRepository apiUserRepository, ResetTokenService resetTokenService, PasswordEncoder passwordEncoder) {
        this.apiUserRepository = apiUserRepository;
        this.resetTokenService = resetTokenService;
        this.passwordEncoder = passwordEncoder;
    }

//    public Optional<User> findByToken(String token) {
//        Optional<ApiUser> customer= customerRepository.findByToken(token);
//        if(customer.isPresent()){
//            ApiUser customer1 = customer.get();
//            User user= new User(customer1.getUserName(), customer1.getPassword(), true, true, true, true,
//                    AuthorityUtils.createAuthorityList("USER"));
//            return Optional.of(user);
//        }
//        return  Optional.empty();
//    }

//    public ApiUser findByEmail(String username, String traceId) throws ResourceNotFoundException {
//        return findByEmail(username, false, traceId);
//    }

    public ApiUser findByEmail(String username, String traceId) throws ResourceNotFoundException {
        Optional<ApiUser> user = apiUserRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new ResourceNotFoundException(traceId, "User not found");
    }

    public ApiUser findById(Long id, String traceId) throws ResourceNotFoundException {
        Optional<ApiUser> apiUser = apiUserRepository.findById(id);
        if (apiUser.isPresent()) {
            return apiUser.get();
        }
        throw new ResourceNotFoundException(traceId, "User not found");
    }


    public ApiUser save(ApiUser user, String tradeId) {
        return apiUserRepository.save(user);
    }

    public UserToRegister registerNewUser(UserToRegister user, String tradeId) throws ResourceAlreadyExistsException {
        ApiUser apiUser = new ApiUser(user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                passwordEncoder.encode(user.getPassword()));
        if (!apiUserRepository.findByUsername(apiUser.getUsername()).isPresent()) {
            apiUserRepository.save(apiUser);
            user.setPassword("*");
            return user;
        }
        throw new ResourceAlreadyExistsException("Email already registered", tradeId);
    }

    public ApiUser changePasswordFromResetToken(PasswordResetRequest passwordResetRequest, String traceId) throws ResourceNotFoundException, TokenException {


        ResetToken resetToken = resetTokenService.findByResetToken(passwordResetRequest.getToken(), traceId);
        if (resetToken.isEnabled()) {
            ApiUser apiUserToUpdate = resetToken.getApiUser();
            apiUserToUpdate.setPassword(passwordEncoder.encode(passwordResetRequest.getPassword()));

            ApiUser apiUserToReturn = apiUserRepository.save(apiUserToUpdate);

            apiUserToReturn.setPassword("*");
            return apiUserToReturn;
        }
        throw new TokenException(traceId, "this password reset token hasn't been validated. validate it through EP: /reset?confirm={your-reset-token-received");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return apiUserRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }


}
