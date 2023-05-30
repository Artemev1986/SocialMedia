package com.example.socialmedia.service;

import com.example.socialmedia.entity.Image;
import com.example.socialmedia.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;

    public Image getById(long id) {
        Image image = imageRepository.getById(id);
        log.debug("Got image by id {}", image.getId());
        return image;
    }
}
