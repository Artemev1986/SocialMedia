package com.example.socialmedia.service;

import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.dto.UserShortDto;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.entity.StatusFriendship;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.mapper.UserMapper;
import com.example.socialmedia.repository.FriendshipRepository;
import com.example.socialmedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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

    public Friendship addFriend(String email, long friendId) {
        User user = userRepository.findByEmail(email);
        User friend = userRepository.getById(friendId);
        if (user.getId().equals(friendId)) {
            throw new ForbiddenException(String.format("user1 (%d) and user2 (%d) are the same",
                    user.getId(), friendId));
        }
        Friendship friendship = new Friendship();
        friendship.setUserId(user.getId());
        friendship.setFriendId(friendId);
        Friendship opponentFriendship = friendshipRepository.getFriendshipByUserIdAndFriendId(friendId, user.getId());
        if (opponentFriendship == null) {
            friendship.setStatus(StatusFriendship.SUBSCRIBE);
        } else {
            friendship.setStatus(StatusFriendship.FRIENDSHIP);
            opponentFriendship.setStatus(StatusFriendship.FRIENDSHIP);
            friendshipRepository.save(opponentFriendship);
            log.debug("user {} subscribed on {} and get status FRIENDSHIP", user, friend);
        }
        friendshipRepository.save(friendship);
        log.debug("user {} subscribed on {}", user, friend);
        return friendship;
    }

    public void declineFriendship(String email, long friendId) {
        User user = userRepository.findByEmail(email);
        if (user.getId().equals(friendId)) {
            throw new ForbiddenException(String.format("user1 (%d) and user2 (%d) are the same",
                    user.getId(), friendId));
        }
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

    public List<UserShortDto> friends(String email) {
        User user = userRepository.findByEmail(email);
        List<Long> friendIds = friendshipRepository.getFriendsByUserId(user.getId());
        List<UserShortDto> friends = userRepository.getUsersByUserList(friendIds).stream()
                .map(UserMapper.INSTANCE::toUserShortDto).collect(Collectors.toList());
        log.debug("Got friend list");
        return friends;
    }

    public List<UserShortDto> subscriptions(String email) {
        User user = userRepository.findByEmail(email);
        List<Long> subscriptionIds = friendshipRepository.getSubscriptionsByUserId(user.getId());
        List<UserShortDto> subscriptions = userRepository.getUsersByUserList(subscriptionIds).stream()
                .map(UserMapper.INSTANCE::toUserShortDto).collect(Collectors.toList());
        log.debug("Got subscription list");
        return subscriptions;
    }
}
