package com.example.socialmedia.controller;

import com.example.socialmedia.controller.util.ControllerUtil;
import com.example.socialmedia.dto.*;
import com.example.socialmedia.entity.Image;
import com.example.socialmedia.service.ImageService;
import com.example.socialmedia.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ImageService imageService;
    private final ControllerUtil controllerUtil;
    private static final String AUTHORIZATION = "Authorization";


    @PostMapping
    ResponseEntity<ResponseNewPost> addPost(
            @RequestPart @NotBlank String title,
            @RequestPart @NotBlank String text,
            @RequestPart MultipartFile[] images,
            @RequestParam @NotBlank @Email String email,
            @RequestHeader(AUTHORIZATION) String token) throws IOException {

        controllerUtil.validateTokenAndEmail(email, token);

        RequestPost newPost = new RequestPost();
        newPost.setTitle(title);
        newPost.setText(text);
        ResponseNewPost postDto = postService.addPost(newPost, images, email);

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponsePost> getPostById(@RequestHeader(AUTHORIZATION) String token,
                                            @NotBlank @Email @RequestParam String email,
                                            @PathVariable Long postId) {
        controllerUtil.validateTokenAndEmail(email, token);

        ResponsePost responsePost = postService.findById(postId);

        if (responsePost != null) {
            return new ResponseEntity<>(responsePost, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{postId}/images/{imageId}")
    public ResponseEntity<Resource> downloadImage(@RequestHeader(AUTHORIZATION) String token,
                                                  @NotBlank @Email @RequestParam String email,
                                                  @PathVariable Long postId,
                                                  @PathVariable Long imageId) {
        controllerUtil.validateTokenAndEmail(email, token);

        Image image = imageService.getById(imageId);

        if (image != null && image.getPost().getId().equals(postId)) {
            ByteArrayResource resource = new ByteArrayResource(image.getImageData());

            return ResponseEntity.ok()
                    .contentLength(image.getImageData().length)
                    .contentType(MediaType.valueOf(image.getContentType()))
                    .body(resource);
        } else {
            throw new EntityNotFoundException(
                    String.format("Post (%d) does not contain images (%d)", postId, imageId));
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@RequestHeader(AUTHORIZATION) String token,
                                           @NotBlank @Email @RequestParam String email,
                                           @PathVariable Long postId) {
        controllerUtil.validateTokenAndEmail(email, token);

        postService.deletePostById(postId, email);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResponseUpdatePost> updatePost(@PathVariable Long postId,
                                                         @RequestPart(required = false) @NotBlank String title,
                                                         @RequestPart(required = false) @NotBlank String text,
                                                         @RequestPart(required = false) MultipartFile[] images,
                                                         @RequestParam(required = false) Long[] deleteImageIds,
                                                         @RequestHeader(AUTHORIZATION) String token,
                                                         @RequestParam @NotBlank @Email String email) throws IOException {
        controllerUtil.validateTokenAndEmail(email, token);

        RequestPost updatePost = new RequestPost();
        updatePost.setId(postId);
        updatePost.setTitle(title);
        updatePost.setText(text);

        ResponseUpdatePost updatedPost = postService.updatePost(updatePost, images, deleteImageIds, email);

        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }
}
