package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Image;
import com.example.socialmedia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    default Image getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Image with id (%d) not found", id))
        );
    }

    @Query("SELECT image.id FROM Image image WHERE image.post = :post")
    List<Long> getImagesByPost(Post post);
}
