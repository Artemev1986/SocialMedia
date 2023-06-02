package com.example.socialmedia.service;

import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.entity.Message;
import com.example.socialmedia.entity.StatusFriendship;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.exception.ForbiddenException;
import com.example.socialmedia.mapper.MessageMapper;
import com.example.socialmedia.repository.FriendshipRepository;
import com.example.socialmedia.repository.MessageRepository;
import com.example.socialmedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final MessageRepository messageRepository;



    public ResponseMessage addMessage(String email, NewMessage newMessage) {
        User sender = userRepository.findByEmail(email);
        User recipient = userRepository.getById(newMessage.getRecipientId());

        if (sender.equals(recipient)) {
            throw new ForbiddenException(String.format("user1 (%d) and user2 (%d) are the same",
                    sender.getId(), recipient.getId()));
        }
        Friendship friendship = friendshipRepository
                .getFriendshipByUserIdAndFriendId(sender.getId(), newMessage.getRecipientId());

        if (friendship == null) {
            throw new ForbiddenException(String.format("there is no relationship between user (%d) and user (%d)",
                            sender.getId(), recipient.getId()));
        }

        if (friendship.getStatus() != StatusFriendship.FRIENDSHIP) {
            throw new ForbiddenException(String.format("there is no friendship between user (%d) and user (%d)",
                    sender.getId(), recipient.getId()));
        }

        Message message = MessageMapper.INSTANCE.toMessage(newMessage, sender, recipient);

        messageRepository.save(message);
        log.debug("sender with id {} sent message to recipient with id {}", sender.getId(), recipient.getId());
        return MessageMapper.INSTANCE.toResponseMessage(message);
    }

    public List<ResponseMessage> getMessagesBetweenUsers(String email, Long friendId, int from, int size) {
        User user = userRepository.findByEmail(email);
        User friend = userRepository.getById(friendId);
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdAt").descending());
        List<ResponseMessage> messages = messageRepository.getMessagesBySenderAndRecipient(user, friend, page)
                .stream().map(MessageMapper.INSTANCE::toResponseMessage).collect(Collectors.toList());
        log.info("Getting messages between users {} and {}", user, friend);
        return messages;
    }

    public List<ResponseMessage> getOutMessagesByUser(String email, int from, int size) {
        User user = userRepository.findByEmail(email);
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdAt").descending());
        List<ResponseMessage> messages = messageRepository.getMessagesBySender(user, page)
                .stream().map(MessageMapper.INSTANCE::toResponseMessage).collect(Collectors.toList());
        log.info("Getting user's {} outgoing messages", user);
        return messages;
    }

    public List<ResponseMessage> getInMessagesByUser(String email, int from, int size) {
        User user = userRepository.findByEmail(email);
        Pageable page = PageRequest.of(from / size, size, Sort.by("createdAt").descending());
        List<ResponseMessage> messages = messageRepository.getMessagesByRecipient(user, page)
                .stream().map(MessageMapper.INSTANCE::toResponseMessage).collect(Collectors.toList());
        log.info("Getting user's {} incoming messages", user);
        return messages;
    }

    public ResponseMessage getMessageById(String email, Long messageId) {
        User user = userRepository.findByEmail(email);
        Message message = messageRepository.getById(messageId);
        if (!(user.getId().equals(message.getSender().getId()) || user.getId().equals(message.getRecipient().getId()))) {
            throw new ForbiddenException(String.format("user with email (%s) does not have permission to read message with id (%d)",
                    email, messageId));
        }
        log.debug("user with email {} read message with id {}", email, messageId);
        return MessageMapper.INSTANCE.toResponseMessage(message);
    }
}
