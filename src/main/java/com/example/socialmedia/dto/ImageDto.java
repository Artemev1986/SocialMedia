package com.example.socialmedia.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO object representing an image")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImageDto {
    @Schema(description = "Image identifier")
    private Long id;

    @Schema(description = "Image name")
    private String name;
}

