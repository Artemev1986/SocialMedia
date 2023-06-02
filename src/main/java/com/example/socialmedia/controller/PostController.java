package com.example.socialmedia.controller;

import com.example.socialmedia.dto.*;
import com.example.socialmedia.entity.Image;
import com.example.socialmedia.exception.ApiError;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.ImageService;
import com.example.socialmedia.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "Post Controller", description = "API endpoints for managing posts and images")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ImageService imageService;
    private final JwtProvider jwtProvider;


    @Operation(summary = "Add a new post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post added successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePost.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PostMapping
    ResponseEntity<ResponsePost> addPost(@Parameter(description = "The title of the post", required = true)
                                         @RequestPart @NotBlank String title,
                                         @Parameter(description = "The text of the post", required = true)
                                         @RequestPart @NotBlank String text,
                                         @Parameter(description = "The images associated with the post")
                                         @RequestPart MultipartFile[] images,
                                         @Parameter(description = "The authorization token", required = true,
                                                 example = "Bearer <token>")
                                         @RequestHeader(AUTHORIZATION) String token) throws IOException {

        RequestPost newPost = new RequestPost();
        newPost.setTitle(title);
        newPost.setText(text);
        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponsePost postDto = postService.addPost(newPost, images, email);

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePost.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PutMapping("/{postId}")
    public ResponseEntity<ResponsePost> updatePost(@Parameter(description = "The ID of the post", required = true)
                                                       @PathVariable Long postId,
                                                   @Parameter(description = "The updated title of the post")
                                                       @RequestPart(required = false) @NotBlank String title,
                                                   @Parameter(description = "The updated text of the post")
                                                       @RequestPart(required = false) @NotBlank String text,
                                                   @Parameter(description = "The updated images associated with the post")
                                                       @RequestPart(required = false) MultipartFile[] images,
                                                   @Parameter(description = "The IDs of images to delete")
                                                       @RequestParam(required = false) Long[] deleteImageIds,
                                                   @Parameter(description = "The authorization token", required = true,
                                                           example = "Bearer <token>")
                                                   @RequestHeader(AUTHORIZATION) String token) throws IOException {

        RequestPost updatePost = new RequestPost();
        updatePost.setId(postId);
        updatePost.setTitle(title);
        updatePost.setText(text);

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponsePost updatedPost = postService.updatePost(updatePost, images, deleteImageIds, email);

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @Operation(summary = "Get a post by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePost.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/{postId}")
    public ResponseEntity<ResponsePost> getPostById(
            @Parameter(description = "The ID of the post", required = true)
            @PathVariable Long postId) {

        ResponsePost responsePost = postService.findById(postId);

        return new ResponseEntity<>(responsePost, HttpStatus.OK);
    }

    @Operation(summary = "Download an image associated with a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image downloaded successfully",
                    content = { @Content(mediaType = "image/*") }),
            @ApiResponse(responseCode = "404", description = "Post or image not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/{postId}/images/{imageId}")
    public ResponseEntity<Resource> downloadImage(
            @Parameter(description = "The ID of the post", required = true)
            @PathVariable Long postId,
            @Parameter(description = "The ID of the image", required = true)
            @PathVariable Long imageId) {

        Image image = imageService.getById(imageId, postId);

            ByteArrayResource resource = new ByteArrayResource(image.getImageData());

            return ResponseEntity.ok()
                    .contentLength(image.getImageData().length)
                    .contentType(MediaType.valueOf(image.getContentType()))
                    .body(resource);
    }

    @Operation(summary = "Delete a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(
            description = "The authorization token", required = true,
            example = "Bearer <token>")
            @RequestHeader(name = "Authorization") String token,
            @Parameter(description = "The ID of the post", required = true)
    @PathVariable Long postId) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        postService.deletePostById(postId, email);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get posts for a subscriber")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponsePost.class)) }),
            @ApiResponse(responseCode = "204", description = "No posts found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<List<ResponsePost>> getPostsForSubscriber(
            @Parameter(description = "The authorization token", required = true,
            example = "Bearer <token>")
            @RequestHeader(name = "Authorization") String token,
            @Parameter(description = "The starting index of posts to retrieve", required = true, example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Parameter(description = "The number of posts to retrieve", required = true, example = "10")
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponsePost> posts = postService.getPostsForSubscriber(email, from, size);
        if (posts.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }
}
