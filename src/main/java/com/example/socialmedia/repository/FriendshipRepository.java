package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    default Friendship getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Friendship with id (%d) not found", id))
        );
    }
}
