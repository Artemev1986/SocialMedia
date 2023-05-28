package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id (%d) not found", id))
        );
    }
}
