package com.example.socialmedia.controller;

import com.example.socialmedia.controller.util.ControllerUtil;
import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.service.MessageService;
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
@RequestMapping(value = "/messages")
@Validated
public class MessageController {

    private final MessageService messageService;
    private final ControllerUtil controllerUtil;
    private static final String AUTHORIZATION = "Authorization";

    @PostMapping
    public ResponseEntity<ResponseMessage> sendMessage(@RequestBody @Valid NewMessage newMessage,
                                                       @RequestHeader(AUTHORIZATION) String token,
                                                       @NotBlank @Email @RequestParam String email) {

        controllerUtil.validateTokenAndEmail(email, token);
        ResponseMessage responseMessage = messageService.addMessage(email, newMessage);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @GetMapping("/main")
    public ResponseEntity<List<ResponseMessage>> getMessagesBetweenUsers(@NotNull @RequestParam Long friendId,
                                                                         @RequestHeader(AUTHORIZATION) String token,
                                                                         @NotBlank @Email @RequestParam String email,
                                                                         @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                         @Positive @RequestParam(defaultValue = "10") Integer size) {

        controllerUtil.validateTokenAndEmail(email, token);
        List<ResponseMessage> messages = messageService.getMessagesBetweenUsers(email, friendId, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<ResponseMessage>> getOutMessagesByUser(@RequestHeader(AUTHORIZATION) String token,
                                                                      @NotBlank @Email @RequestParam String email,
                                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {

        controllerUtil.validateTokenAndEmail(email, token);
        List<ResponseMessage> messages = messageService.getOutMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/income")
    public ResponseEntity<List<ResponseMessage>> getInMessagesByUser(@RequestHeader(AUTHORIZATION) String token,
                                                                     @NotBlank @Email @RequestParam String email,
                                                                     @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                     @Positive @RequestParam(defaultValue = "10") Integer size) {

        controllerUtil.validateTokenAndEmail(email, token);
        List<ResponseMessage> messages = messageService.getInMessagesByUser(email, from, size);
        if (messages.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ResponseMessage> getMessageById(@PathVariable long messageId,
                                                          @RequestHeader(AUTHORIZATION) String token,
                                                          @NotBlank @Email @RequestParam String email) {

        controllerUtil.validateTokenAndEmail(email, token);
        ResponseMessage message = messageService.getMessageById(email, messageId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}