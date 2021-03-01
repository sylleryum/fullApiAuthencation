package com.sylleryum.sylleryum.entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({ "accessToken", "validity", "refreshToken"})
public class ResponseToken {

    private String accessToken;
    private LocalDate validity;
    private RefreshToken refreshToken;

    public ResponseToken() {
    }

    public ResponseToken(String accessToken, LocalDate validity, RefreshToken refreshToken) {
        this.accessToken = accessToken;
        this.validity = validity;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getValidity() {
        return validity.toString();
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }
}
