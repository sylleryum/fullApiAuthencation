package com.sylleryum.sylleryum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@JsonPropertyOrder({ "refreshToken", "validity"})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    private String refreshToken;
    @OneToOne
    @JoinColumn(name = "apiUser_id", referencedColumnName = "id")
    @JsonIgnore
    private ApiUser apiUser;
    private LocalDate validity;

    @Transient
    private final String refreshEndPoint = "/refresh";

    public RefreshToken(ApiUser apiUser, LocalDate validity) {
        this.refreshToken = "REF"+UUID.randomUUID().toString();
        this.apiUser = apiUser;
        this.validity = validity;
    }

    public RefreshToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApiUser getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUser user) {
        this.apiUser = user;
    }

    public String getValidity() {
        return validity.toString();
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public String getRefreshEndPoint() {
        return refreshEndPoint;
    }
}
