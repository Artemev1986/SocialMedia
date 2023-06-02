package com.example.socialmedia.service;

import com.example.socialmedia.dto.RequestPost;
import com.example.socialmedia.dto.ResponsePost;
import com.example.socialmedia.entity.Image;
import com.example.socialmedia.entity.Post;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.mapper.PostMapper;
import com.example.socialmedia.repository.FriendshipRepository;
import com.example.socialmedia.repository.ImageRepository;
import com.example.socialmedia.repository.PostRepository;
import com.example.socialmedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final ImageRepository imageRepository;

    public ResponsePost findById(long id) {
        Post post = postRepository.getById(id);
        List<Long> imageIds = imageRepository.getImagesByPost(post);
        post.setImageIds(imageIds);
        ResponsePost responsePost = PostMapper.INSTANCE.toResponsePost(post);
        log.debug("Got post {}", responsePost);
        return responsePost;
    }

    public ResponsePost addPost(RequestPost newPost, MultipartFile[] newImages, String email) throws IOException {
        User user = userRepository.findByEmail(email);

        Post post = PostMapper.INSTANCE.toPost(newPost, user);

        List<Image> imageDataList = new ArrayList<>();

        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    byte[] imageData = file.getBytes();
                    Image image = new Image();
                    image.setPost(post);
                    image.setImageData(imageData);
                    image.setContentType(file.getContentType());
                    image.setName(file.getOriginalFilename());
                    imageDataList.add(image);
                }
            }
        }

        postRepository.save(post);
        imageRepository.saveAll(imageDataList);
        post.setImageIds(imageDataList.stream().map(Image::getId).collect(Collectors.toList()));
        ResponsePost responseNewPost = PostMapper
                .INSTANCE.toResponsePost(post);
        log.debug("Added new post: {} by user: {}", post, user);
        return responseNewPost;
    }

    public ResponsePost updatePost(RequestPost updatePost,
                                         MultipartFile[] newImages,
                                         Long[] deleteImageIds,
                                         String email) throws IOException {
        User user = userRepository.findByEmail(email);
        Post post = postRepository.getById(updatePost.getId());

        if (!email.equals(post.getUser().getEmail())) {
            throw new ForbiddenException("This user can't update this post");
        }

        List<Long> imageIds = imageRepository.getImagesByPost(post);
        List<Long> imageIdsToRemove = new ArrayList<>();

        if (deleteImageIds != null && deleteImageIds.length > 0) {
            for (Long imageId: deleteImageIds) {
                if (imageIds.contains(imageId)) {
                    imageIdsToRemove.add(imageId);
                }
            }
            if (!imageIdsToRemove.isEmpty()) {
                imageRepository.deleteAllById(imageIdsToRemove);
            }
        }

        List<Image> imageDataList = new ArrayList<>();

        if (newImages != null) {
            for (MultipartFile file : newImages) {
                if (!file.isEmpty()) {
                    byte[] imageData = file.getBytes();
                    Image image = new Image();
                    image.setPost(post);
                    image.setImageData(imageData);
                    image.setContentType(file.getContentType());
                    image.setName(file.getOriginalFilename());
                    imageDataList.add(image);
                }
            }
        }

        if (!imageDataList.isEmpty())
        {
            imageRepository.saveAll(imageDataList);
        }

        post.setImageIds(imageRepository.getImagesByPost(post));

        if (!updatePost.getTitle().isEmpty()) {
            post.setTitle(updatePost.getTitle());
        }

        if (!updatePost.getText().isEmpty()) {
            post.setText(updatePost.getText());
        }

        post.setUpdatedAt(LocalDateTime.now());

        ResponsePost responseUpdatePost = PostMapper
                .INSTANCE.toResponsePost(postRepository.save(post));
        log.debug("Updated post: {} by user: {}", post, user);
        return responseUpdatePost;
    }

    public List<ResponsePost> getPostsForSubscriber(String email, int from, int size) {
        User user = userRepository.findByEmail(email);
        List<Long> userIds = friendshipRepository.getMainSubscriptionsByUserId(user.getId());
        if (userIds.isEmpty()) {
            log.info("Getting empty post list for subscriber with id {}", user.getId());
            return new ArrayList<>();
        }
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdAt").descending());
        List<ResponsePost> posts = postRepository.getPostByUsers(userIds, page).stream().map(post -> {
            post.setImageIds(imageRepository.getImagesByPost(post));
            return PostMapper.INSTANCE.toResponsePost(post);
                }).collect(Collectors.toList());
        log.info("Getting posts for subscriber with id {}", user.getId());
        return posts;
    }

    public void deletePostById(Long postId, String email) {
        userRepository.findByEmail(email);
        Post post = postRepository.getById(postId);
        if (!email.equals(post.getUser().getEmail())) {
            throw new ForbiddenException("This user can't delete this post");
        }
        postRepository.deleteById(postId);
        log.debug("Post with id {} was deleted", postId);
    }
}
