package com.example.socialmedia.controller;

import com.example.socialmedia.dto.AuthenticationRequest;
import com.example.socialmedia.dto.AuthenticationResponse;
import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.UserDto;
import com.example.socialmedia.entity.User;
import com.example.socialmedia.mapper.UserMapper;
import com.example.socialmedia.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }
    @Sql("classpath:cleanup-script.sql")
    @Test
    void registerAndAuthenticationOk() throws Exception {
        RegistrationRequest newUser = new RegistrationRequest();
        newUser.setName("Mik");
        newUser.setEmail("mik@mail.ru");
        newUser.setPassword("password");

        User user = UserMapper.INSTANCE.registrationToUser(newUser);
        user.setId(1L);
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);

        mockMvc.perform(post("/api/registration")
                        .content(objectMapper.writeValueAsString(newUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail(newUser.getEmail());
        authenticationRequest.setPassword(newUser.getPassword());

        String token = "token";

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setToken(token);

        Mockito
                .when(jwtProvider.createToken(Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(token);

        mockMvc.perform(post("/api/authentication")
                        .content(objectMapper.writeValueAsString(authenticationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authenticationResponse)));
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void registerAndAuthenticationError() throws Exception {
        RegistrationRequest newUser = new RegistrationRequest();
        newUser.setName("Mik");
        newUser.setEmail("mik@mail.ru");
        newUser.setPassword("password");

        User user = UserMapper.INSTANCE.registrationToUser(newUser);
        user.setId(1L);
        UserDto userDto = UserMapper.INSTANCE.toUserDto(user);

        mockMvc.perform(post("/api/registration")
                        .content(objectMapper.writeValueAsString(newUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail(newUser.getEmail());
        authenticationRequest.setPassword("wrong-password");

        mockMvc.perform(post("/api/authentication")
                        .content(objectMapper.writeValueAsString(authenticationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void userAlreadyExistError() throws Exception {
        RegistrationRequest newUser = new RegistrationRequest();
        newUser.setName("Mik");
        newUser.setEmail("mik@mail.ru");
        newUser.setPassword("password");

        mockMvc.perform(post("/api/registration")
                        .content(objectMapper.writeValueAsString(newUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/registration")
                        .content(objectMapper.writeValueAsString(newUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void notFoundUserAuthenticationError() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setEmail("test@mail.ru");
        authenticationRequest.setPassword("password");

        mockMvc.perform(post("/api/authentication")
                        .content(objectMapper.writeValueAsString(authenticationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
