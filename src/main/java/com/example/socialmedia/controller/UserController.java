package com.example.socialmedia.controller;

import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.dto.UserShortDto;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.MessageService;
import com.example.socialmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final MessageService messageService;
    private static final String AUTHORIZATION = "Authorization";

    @PutMapping("/friends/{friendId}")
    public ResponseEntity<Friendship> addFriendship(@PathVariable long friendId,
                                                    @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        Friendship friendship = userService.addFriend(email, friendId);
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserShortDto>> getFriends(@RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<UserShortDto> friends = userService.friends(email);
        if (friends.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<UserShortDto>> getSubscriptions(@RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<UserShortDto> subscriptions = userService.subscriptions(email);
        if (subscriptions.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> declineFriendship(@PathVariable long friendId,
                                               @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        userService.declineFriendship(email, friendId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages")
    public ResponseEntity<ResponseMessage> sendMessage(@RequestBody @Valid NewMessage newMessage,
                                                       @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponseMessage responseMessage = messageService.addMessage(email, newMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/messages/main")
    public ResponseEntity<List<ResponseMessage>> getMessagesBetweenUsers(@NotNull @RequestParam Long friendId,
                                                                         @RequestHeader(AUTHORIZATION) String token,
                                                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                         @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getMessagesBetweenUsers(email, friendId, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/outgoing")
    public ResponseEntity<List<ResponseMessage>> getOutMessagesByUser(@RequestHeader(AUTHORIZATION) String token,
                                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getOutMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/income")
    public ResponseEntity<List<ResponseMessage>> getInMessagesByUser(@RequestHeader(AUTHORIZATION) String token,
                                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getInMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<ResponseMessage> getMessageById(@PathVariable long messageId,
                                                          @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponseMessage message = messageService.getMessageById(email, messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}