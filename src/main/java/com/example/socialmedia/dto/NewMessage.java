package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a new message")
public class NewMessage {
    @NotNull
    @Schema(description = "Recipient identifier")
    private Long recipientId;

    @NotBlank
    @Schema(description = "Message text")
    private String text;
}
