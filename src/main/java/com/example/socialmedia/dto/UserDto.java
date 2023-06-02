package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a user")
public class UserDto {
    @Schema(description = "User ID", example = "1")
    private Long id;
    @Schema(description = "User email")
    private String email;
    @Schema(description = "User name")
    private String name;
}
