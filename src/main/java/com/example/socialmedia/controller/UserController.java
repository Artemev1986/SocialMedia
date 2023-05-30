package com.example.socialmedia.controller;

import com.example.socialmedia.controller.util.ControllerUtil;
import com.example.socialmedia.entity.Friendship;
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
    private final ControllerUtil controllerUtil;
    private static final String AUTHORIZATION = "Authorization";

    @PutMapping("/friends/{friendId}")
    public ResponseEntity<Friendship> addFriendship(@PathVariable long friendId,
                                                    @RequestHeader(AUTHORIZATION) String token,
                                                    @NotBlank @Email @RequestParam String email) {

        controllerUtil.validateTokenAndEmail(email, token);
        Friendship friendship = userService.addFriend(email, friendId);
        return new ResponseEntity<>(friendship, HttpStatus.OK);
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<?> declineFriendship(@PathVariable long friendId,
                                               @RequestHeader(AUTHORIZATION) String token,
                                               @NotBlank @Email @RequestParam String email) {

        controllerUtil.validateTokenAndEmail(email, token);
        userService.declineFriendship(email, friendId);
        return ResponseEntity.ok().build();
    }
}