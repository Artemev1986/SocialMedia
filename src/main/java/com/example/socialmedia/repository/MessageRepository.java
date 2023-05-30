package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Message;
import com.example.socialmedia.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    default Message getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Message with id (%d) not found", id))
        );
    }

    @Query("SELECT mes FROM Message mes JOIN FETCH mes.sender JOIN FETCH mes.recipient WHERE " +
            "mes.sender = :sender AND mes.recipient = :recipient " +
            "OR mes.sender = :recipient AND mes.recipient = :sender")
    List<Message> getMessagesBySenderAndRecipient(User sender, User recipient, Pageable page);

    List<Message> getMessagesBySender(User sender, Pageable page);

    List<Message> getMessagesByRecipient(User recipient, Pageable page);
}
