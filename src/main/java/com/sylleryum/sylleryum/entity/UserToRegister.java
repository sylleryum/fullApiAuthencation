package com.sylleryum.sylleryum.entity;

import org.springframework.validation.BindingResult;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserToRegister {

    @NotEmpty(message = "First name cannot be empty")
    @NotNull
    private String firstName;
    @NotNull
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @Email(message = "Please validate the email")
    @NotNull
    @NotEmpty(message = "Email cannot be empty")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    @NotNull
    private String password;

    public UserToRegister() {
    }

    public UserToRegister(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    /**
     * params correct = "", params with problem = "error message"
     * @param bindingResult
     */
    public UserToRegister(BindingResult bindingResult){
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.password = "";

        if (bindingResult.getFieldError("firstName")!=null){
            this.firstName=bindingResult.getFieldError("firstName").getDefaultMessage();
        }

        if (bindingResult.getFieldError("lastName")!=null){
            this.lastName=bindingResult.getFieldError("lastName").getDefaultMessage();
        }

        if (bindingResult.getFieldError("email")!=null){
            this.email=bindingResult.getFieldError("email").getDefaultMessage();
        }

        if (bindingResult.getFieldError("password")!=null){
            this.password=bindingResult.getFieldError("password").getDefaultMessage();
        }
        System.out.println();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
