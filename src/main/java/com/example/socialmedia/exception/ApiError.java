package com.example.socialmedia.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Represents an API error")
public class ApiError {
    @Schema(description = "Error message")
    private String message;
    @Schema(description = "Error status")
    private String status;

    @Schema(description = "Timestamp when the error occurred")
    private String timestamp;
}