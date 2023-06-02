package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Represents a request for a post")
public class RequestPost {
    @Schema(description = "Post ID", example = "1")
    private Long id;

    @NotBlank
    @Schema(description = "Post title", example = "Hello World")
    private String title;

    @NotBlank
    @Schema(description = "Post text", example = "This is a sample post")
    private String text;
}
