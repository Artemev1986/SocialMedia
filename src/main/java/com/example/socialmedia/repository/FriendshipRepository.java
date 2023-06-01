package com.example.socialmedia.repository;

import com.example.socialmedia.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.EntityNotFoundException;
import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    default Friendship getById(Long id) {
        return findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Friendship with id (%d) not found", id))
        );
    }

    Friendship getFriendshipByUserIdAndFriendId(long userId, long friendId);

    @Query("SELECT fr.userId FROM Friendship fr WHERE fr.userId = :userId")
    List<Long> getFriendshipsByUserId(long userId);
}
