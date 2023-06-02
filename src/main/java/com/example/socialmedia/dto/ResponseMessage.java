package com.example.socialmedia.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a response message")
public class ResponseMessage {
    @Schema(description = "Message ID", example = "1")
    private Long id;

    @Schema(description = "Message text", example = "Hello, how are you?")
    private String text;

    @Schema(description = "Sender information")
    private UserShortDto sender;

    @Schema(description = "Recipient information")
    private UserShortDto recipient;

    @Schema(description = "Creation timestamp", example = "2023-06-01 12:34:56")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
