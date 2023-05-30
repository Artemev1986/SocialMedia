package com.example.socialmedia.service;

import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.entity.StatusFriendship;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.repository.FriendshipRepository;
import com.example.socialmedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendshipRepository friendshipRepository;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public User findByEmailAndPassword(String email, String password) {
        User user = findUserByEmail(email);
        log.debug("finding user by email {} and password {}", email, password);
        if (passwordEncoder.matches(password, user.getPassword())) {
            log.debug("user found");
            return user;
        } else {
            throw new EntityNotFoundException("There is no " + email + " in the database with this password");
        }
    }

    public void addFriend(String email, long friendId) {
        User user = findUserByEmail(email);
        User friend = userRepository.getById(friendId);
        Friendship friendship = new Friendship();
        friendship.setUserId(user.getId());
        friendship.setFriendId(friendId);
        friendship.setStatus(StatusFriendship.SUBSCRIBE);
        friendshipRepository.save(friendship);
        log.debug("user {} subscribed on {}", user, friend);
    }

    public void confirmFriendship(String email, long friendshipId) {
        User user = findUserByEmail(email);
        Friendship friendship = friendshipRepository.getById(friendshipId);
        if (!friendship.getFriendId().equals(user.getId())) {
            throw new ForbiddenException(
                    String.format("User with id (%d) can't confirm friendship with id (%d)", user.getId(), friendshipId));
        }
        friendship.setStatus(StatusFriendship.FRIENDSHIP);
        friendshipRepository.save(friendship);

        Friendship newFriendship = new Friendship();
        newFriendship.setUserId(user.getId());
        newFriendship.setFriendId(friendship.getUserId());
        newFriendship.setStatus(StatusFriendship.FRIENDSHIP);
        friendshipRepository.save(newFriendship);

        log.debug("user {} confirm friendship {}", user, friendship);
    }

    public void declineFriendship(String email, long friendshipId) {
        User user = findUserByEmail(email);
        Friendship friendship = friendshipRepository.getById(friendshipId);
        if (!friendship.getFriendId().equals(user.getId())) {
            throw new ForbiddenException(
                    String.format("User with id (%d) can't decline friendship with id (%d)", user.getId(), friendshipId));
        }
        friendship.setStatus(StatusFriendship.DECLINE);
        friendshipRepository.save(friendship);

        Friendship confirmedFriendship = friendshipRepository.getFriendshipByUserIdAndFriendId(user.getId(), friendship.getUserId());
        if (confirmedFriendship != null) {
            friendshipRepository.delete(confirmedFriendship);
        }
        log.debug("user {} decline friendship {}", user, friendship);
    }

    private User findUserByEmail(String email) {
        User user = userRepository.findUserByEmail(email);
        log.debug("finding user by email: {}", email);
        if (user == null) {
            throw new EntityNotFoundException("User named " + email + " not found");
        }
        log.debug("user found");
        return user;
    }


}
