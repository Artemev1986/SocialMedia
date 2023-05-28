package com.example.socialmedia.controller;

import com.example.socialmedia.dto.AuthenticationRequest;
import com.example.socialmedia.dto.AuthenticationResponse;
import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.mapper.UserMapper;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;


    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {

        User user = UserMapper.INSTANCE.registrationToUser(registrationRequest);
        userService.registerUser(user);
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody @Valid AuthenticationRequest request) {
        User user = userService.findByEmailAndPassword(request.getEmail(), request.getPassword());

        String token = jwtProvider.generateToken(user.getEmail());
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(token);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}
