package com.example.socialmedia.controller;

import com.example.socialmedia.dto.AuthenticationRequest;
import com.example.socialmedia.dto.AuthenticationResponse;
import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.exception.ApiError;
import com.example.socialmedia.security.JwtProvider;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "Authentication Controller", description = "API endpoints for user authentication and registration")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Operation(summary = "Registration new users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new user",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }) })
    @PostMapping("/registration")
    public ResponseEntity<UserDto> registerUser(
            @Parameter(description = "Request off a new user registration")
            @RequestBody @Valid RegistrationRequest registrationRequest) {

        UserDto userDto = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @Operation(summary = "User authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)) }),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) }),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)) })
    })
    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(
            @Parameter(description = "Authentication request")
            @RequestBody @Valid AuthenticationRequest request) {

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
