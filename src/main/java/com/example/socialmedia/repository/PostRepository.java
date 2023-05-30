package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityNotFoundException;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id (%d) not found", id))
        );
    }

    @Query("SELECT post FROM Post post JOIN FETCH post.user WHERE post.user.id IN :userIds")
    List<Post> getPostByUsers(List<Long> userIds, Pageable page);
}
