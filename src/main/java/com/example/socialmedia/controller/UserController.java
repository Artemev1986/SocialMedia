package com.example.socialmedia.controller;

import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/users")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private static final String AUTHORIZATION = "Authorization";

    @PutMapping("/friends/{friendId}")
    public ResponseEntity<Friendship> addFriendship(@PathVariable long friendId,
                                                    @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        Friendship friendship = userService.addFriend(email, friendId);
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> declineFriendship(@PathVariable long friendId,
                                               @RequestHeader(AUTHORIZATION) String token) {

        String email = jwtProvider.getEmailFromToken(token.substring(7));
        userService.declineFriendship(email, friendId);
        return ResponseEntity.ok().build();
    }
}