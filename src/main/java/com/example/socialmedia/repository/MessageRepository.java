package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityNotFoundException;

public interface MessageRepository extends JpaRepository<Message, Long> {

    default Message getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Message with id (%d) not found", id))
        );
    }
}
