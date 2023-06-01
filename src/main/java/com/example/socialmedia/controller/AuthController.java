package com.example.socialmedia.controller;

import com.example.socialmedia.dto.AuthenticationRequest;
import com.example.socialmedia.dto.AuthenticationResponse;
import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.exception.ApiError;
import com.example.socialmedia.security.JwtProvider;
import com.example.socialmedia.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Operation(summary = "The body of the request is the RegistrationRequest class. Add new user with password to database. " +
            "The body of the response is the UserDto class (it contains id, email, name). " +
            "If a user with the same email already exists or password has not correct format or " +
            "password and confirmPassword don't match or currency has incorrect name, " +
            "an appropriate exception will be thrown")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the book",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }) })
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) {

        UserDto userDto = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }


    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody @Valid AuthenticationRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        boolean rememberMe = request.getIsRememberMe() != null && request.getIsRememberMe();
        String jwt = jwtProvider.createToken(authentication, rememberMe);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(jwt);
        return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }
}
