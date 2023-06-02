package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Authentication response payload")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthenticationResponse {
    @Schema(description = "Access token")
    private String token;
}
