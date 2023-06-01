package com.example.socialmedia.service;

import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.entity.StatusFriendship;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.mapper.UserMapper;
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

    public UserDto registerUser(RegistrationRequest registrationRequest) {
        User user = UserMapper.INSTANCE.registrationToUser(registrationRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return UserMapper.INSTANCE.toUserDto(user);
    }

    public User findByEmailAndPassword(String email, String password) {
        User user = userRepository.findByEmail(email);
        log.debug("finding user by email {} and password {}", email, password);
        if (passwordEncoder.matches(password, user.getPassword())) {
            log.debug("user with email {} and password found", email);
            return user;
        } else {
            throw new EntityNotFoundException("There is no " + email + " in the database with this password");
        }
    }

    public Friendship addFriend(String email, long friendId) {
        User user = userRepository.findByEmail(email);
        User friend = userRepository.getById(friendId);
        Friendship friendship = new Friendship();
        friendship.setUserId(user.getId());
        friendship.setFriendId(friendId);
        Friendship oldFriendship = friendshipRepository.getFriendshipByUserIdAndFriendId(friendId, user.getId());
        if (oldFriendship == null) {
            friendship.setStatus(StatusFriendship.SUBSCRIBE);
        } else {
            friendship.setStatus(StatusFriendship.FRIENDSHIP);
            oldFriendship.setStatus(StatusFriendship.FRIENDSHIP);
            friendshipRepository.save(oldFriendship);
            log.debug("user {} subscribed on {} and get status FRIENDSHIP", user, friend);
        }
        friendshipRepository.save(friendship);
        log.debug("user {} subscribed on {}", user, friend);
        return friendship;
    }

    public void declineFriendship(String email, long friendId) {
        User user = userRepository.findByEmail(email);
        Friendship opponentFriendship = friendshipRepository.getFriendshipByUserIdAndFriendId(friendId, user.getId());
        if (opponentFriendship != null) {
            opponentFriendship.setStatus(StatusFriendship.DECLINE);
            friendshipRepository.save(opponentFriendship);
        }

        Friendship friendship = friendshipRepository.getFriendshipByUserIdAndFriendId(user.getId(), friendId);
        if (friendship != null) {
            friendshipRepository.delete(friendship);
        }

        if (opponentFriendship == null && friendship == null) {
            throw new EntityNotFoundException(
                    String.format("there is no relationship between user (%d) and user (%d)", user.getId(), friendId));
        }
        log.debug("user {} decline friendship by user with id {}", user, friendId);
    }
}
