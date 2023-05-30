package com.example.socialmedia.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewMessage {
    @NotNull
    Long recipientId;
    @NotBlank
    private String text;
}
