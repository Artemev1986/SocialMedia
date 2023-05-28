package com.example.socialmedia.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegistrationRequest {

    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
}