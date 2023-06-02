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
@Schema(description = "Response DTO for a post")
public class ResponsePost {

    @Schema(description = "Post ID", example = "1")
    private Long id;

    @Schema(description = "Post title")
    private String title;

    @Schema(description = "Post text")
    private String text;

    @Schema(description = "User information")
    private UserShortDto user;

    @Schema(description = "List of image IDs")
    private List<Long> imageIds;

    @Schema(description = "Creation timestamp", example = "2023-06-01 12:34:56")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2023-06-01 12:34:56")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
