package com.example.socialmedia.controller;

import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.dto.UserShortDto;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.exception.ApiError;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.MessageService;
import com.example.socialmedia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Tag(name = "User Controller", description = "API endpoints for managing users, friendships, and messages")

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final MessageService messageService;

    @Operation(summary = "Add a friendship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendship added successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Friendship.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "User or friend not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PutMapping("/friends/{friendId}")
    public ResponseEntity<Friendship> addFriendship(
            @Parameter(description = "The ID of the friend", required = true)
            @PathVariable long friendId,
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        Friendship friendship = userService.addFriend(email, friendId);
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }

    @Operation(summary = "Get friends")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserShortDto.class)) }),
            @ApiResponse(responseCode = "204", description = "No friends found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/friends")
    public ResponseEntity<List<UserShortDto>> getFriends(
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<UserShortDto> friends = userService.friends(email);
        if (friends.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @Operation(summary = "Get subscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscriptions found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserShortDto.class)) }),
            @ApiResponse(responseCode = "204", description = "No subscriptions found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<List<UserShortDto>> getSubscriptions(
            @Parameter(description = "The authorization token", required = true,
                    example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<UserShortDto> subscriptions = userService.subscriptions(email);
        if (subscriptions.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @Operation(summary = "Decline a friendship")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendship declined successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> declineFriendship(
            @Parameter(description = "The ID of the friend", required = true)
            @PathVariable long friendId,
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        userService.declineFriendship(email, friendId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Send a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message sent successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PostMapping("/messages")
    public ResponseEntity<ResponseMessage> sendMessage(
            @Parameter(description = "The new message object", required = true,
            schema = @Schema(implementation = NewMessage.class))
            @RequestBody @Valid NewMessage newMessage,
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponseMessage responseMessage = messageService.addMessage(email, newMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @Operation(summary = "Get messages between users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/messages/main")
    public ResponseEntity<List<ResponseMessage>> getMessagesBetweenUsers(
            @Parameter(description = "The ID of the friend", required = true)
            @NotNull @RequestParam Long friendId,
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(name = "Authorization") String token,
            @Parameter(description = "The starting index of messages to retrieve", required = true, example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Parameter(description = "The number of messages to retrieve", required = true, example = "10")
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getMessagesBetweenUsers(email, friendId, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Operation(summary = "Get outgoing messages by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/messages/outgoing")
    public ResponseEntity<List<ResponseMessage>> getOutMessagesByUser(
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(name = "Authorization") String token,
            @Parameter(description = "The starting index of messages to retrieve", required = true, example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Parameter(description = "The number of messages to retrieve", required = true, example = "10")
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getOutMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Operation(summary = "Get incoming messages by user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "204", description = "No messages found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/messages/income")
    public ResponseEntity<List<ResponseMessage>> getInMessagesByUser(
            @Parameter(description = "The authorization token", required = true,example = "Bearer <token>")
            @RequestHeader(name = "Authorization") String token,
            @Parameter(description = "The starting index of messages to retrieve", required = true, example = "0")
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Parameter(description = "The number of messages to retrieve", required = true, example = "10")
            @Positive @RequestParam(defaultValue = "10") Integer size) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        List<ResponseMessage> messages = messageService.getInMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @Operation(summary = "Get a message by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "Message not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<ResponseMessage> getMessageById(
            @Parameter(description = "The ID of the message", required = true)
            @PathVariable long messageId,
            @Parameter(description = "The authorization token", required = true, example = "Bearer <token>")
            @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        ResponseMessage message = messageService.getMessageById(email, messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}