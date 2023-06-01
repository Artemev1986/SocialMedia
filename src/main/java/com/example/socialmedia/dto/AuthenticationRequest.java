package com.example.socialmedia.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthenticationRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Boolean isRememberMe;
}

