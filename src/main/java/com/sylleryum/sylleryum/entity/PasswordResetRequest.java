package com.sylleryum.sylleryum.entity;

import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PasswordResetRequest {

    @NotEmpty(message = "Token cannot be empty")
    @NotNull
    private String token;
    @NotEmpty(message = "Password cannot be empty")
    @NotNull
    private String password;

    public PasswordResetRequest() {
    }

    public PasswordResetRequest(BindingResult bindingResult) {
        this.token = "";
        this.password = "";
        if (bindingResult.getFieldError("token")!=null){
            this.token=bindingResult.getFieldError("token").getDefaultMessage();
        }
        if (bindingResult.getFieldError("password")!=null){
            this.password=bindingResult.getFieldError("password").getDefaultMessage();
        }
    }

    public String getToken() {return token;}
    public void setToken(String token) {this.token = token;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
