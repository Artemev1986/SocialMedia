package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a registration request")
public class RegistrationRequest {
    @Email
    @NotBlank
    @Schema(description = "User email", example = "example@example.com")
    private String email;

    @NotBlank
    @Schema(description = "User name", example = "John Doe")
    private String name;

    @NotBlank
    @Schema(description = "User password", example = "secretpassword")
    private String password;
}
