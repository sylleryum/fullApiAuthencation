package com.sylleryum.sylleryum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@JsonPropertyOrder({ "refreshToken", "validity"})
public class ResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    private String resetToken;
    @OneToOne
    @JoinColumn(name = "apiUser_id", referencedColumnName = "id")
    @JsonIgnore
    private ApiUser apiUser;
    private LocalDateTime validity;
    private Boolean enabled = false;

    public ResetToken() {
    }

    public ResetToken(ApiUser apiUser, LocalDateTime validity) {
        this.resetToken = "RES"+UUID.randomUUID().toString();
        this.apiUser = apiUser;
        this.validity = validity;
    }

    public ResetToken(ApiUser apiUser, LocalDateTime validity, boolean enabled) {
        this.resetToken = "RES"+UUID.randomUUID().toString();
        this.apiUser = apiUser;
        this.validity = validity;
        this.enabled = enabled;
    }

    public ResetToken(String resetToken, ApiUser apiUser, LocalDateTime validity) {
        this.resetToken = resetToken;
        this.apiUser = apiUser;
        this.validity = validity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public ApiUser getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUser apiUser) {
        this.apiUser = apiUser;
    }

    public LocalDateTime getValidity() {
        return validity;
    }

    public void setValidity(LocalDateTime validity) {
        this.validity = validity;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
