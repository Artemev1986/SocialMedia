package com.example.socialmedia.repository;

import com.example.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    default User getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User with id (%d) not found", id))
        );
    }

    default User findByEmail(String email) {
        User user = findUserByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException(String.format("User with email (%s) not found", email));
        }
        return user;
    }

    @Query("SELECT user FROM User user WHERE user.id IN :userIds")
    List<User> getUsersByUserList(List<Long> userIds);
}
