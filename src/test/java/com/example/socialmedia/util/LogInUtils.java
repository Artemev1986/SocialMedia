package com.example.socialmedia.util;

import com.example.socialmedia.dto.AuthenticationRequest;
import com.example.socialmedia.dto.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public final class LogInUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private LogInUtils() {
    }

    public static String getTokenForLogin(String email, String password, MockMvc mockMvc) throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail(email);
        authenticationRequest.setPassword(password);

        String content = mockMvc.perform(post("/authentication")
                        .content(OBJECT_MAPPER.writeValueAsString(authenticationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationResponse authResponse = OBJECT_MAPPER.readValue(content, AuthenticationResponse.class);

        return authResponse.getToken();
    }
}
