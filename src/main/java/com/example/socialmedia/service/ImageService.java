package com.example.socialmedia.service;

import com.example.socialmedia.entity.Image;
import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;

    public Image getById(long id, long postId) {
        Image image = imageRepository.getById(id);
        if (!image.getPost().getId().equals(postId)) {
            throw new ForbiddenException(
                    String.format("This image with id (%d) is not included in post with id (%d)", id, postId));
        }
        log.debug("Got image by id {}", image.getId());
        return image;
    }
}
