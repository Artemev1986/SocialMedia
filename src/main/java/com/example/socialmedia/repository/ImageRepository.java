package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface ImageRepository extends JpaRepository<Image, Long> {

    default Image getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Image with id (%d) not found", id))
        );
    }
}
