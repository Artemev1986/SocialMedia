package com.example.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    private Boolean isRememberMe;
}

