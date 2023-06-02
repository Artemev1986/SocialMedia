package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Schema(description = "Authentication request payload")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationRequest {

    @Schema(description = "User email", example = "example@example.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "User password", example = "password")
    @NotBlank
    private String password;

    @Schema(description = "Remember me flag")
    private Boolean isRememberMe;
}


