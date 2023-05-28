package com.example.socialmedia.repository;

import com.example.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    default User getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id (%d) not found", id))
        );
    }
}
