package com.example.socialmedia.controller;

import com.example.socialmedia.dto.NewMessage;
import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.ResponseMessage;
import com.example.socialmedia.dto.UserShortDto;
import com.example.socialmedia.entity.Friendship;
import com.example.socialmedia.entity.StatusFriendship;
import com.example.socialmedia.security.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.socialmedia.util.LogInUtils.getTokenForLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = "classpath:application-test.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private RegistrationRequest newUser1;
    private RegistrationRequest newUser2;
    private RegistrationRequest newUser3;
    private String tokenForUser1;
    private String tokenForUser2;
    private String tokenForUser3;
    private Friendship friendship1;
    private Friendship friendship2;
    private NewMessage message1;
    private NewMessage message2;
    private NewMessage message3;
    private ResponseMessage responseMessage1;
    private ResponseMessage responseMessage2;
    private ResponseMessage responseMessage3;
    private List<ResponseMessage> messageList;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        newUser1 = new RegistrationRequest("mik@mail.ru","Mik","password");
        newUser2 = new RegistrationRequest("alex@mail.ru","Alex","password2");
        newUser3 = new RegistrationRequest("oleg@mail.ru","Oleg","password3");
        tokenForUser1 = registerUserAndGetToken(newUser1);
        tokenForUser2 = registerUserAndGetToken(newUser2);
        tokenForUser3 = registerUserAndGetToken(newUser3);
        friendship1 = new Friendship(1L,2L, StatusFriendship.SUBSCRIBE);
        friendship1.setId(1L);
        friendship2 = new Friendship(2L,1L, StatusFriendship.FRIENDSHIP);
        friendship2.setId(2L);
        message1 = new NewMessage(2L, "message1");
        message2 = new NewMessage(1L, "message2");
        message3 = new NewMessage(2L, "message3");
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void toSubscribeAndSendMessageTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performMessageSend("/users/messages", tokenForUser1, message1, status().isForbidden());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void toFriendAndSendMessageTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());
        performMessageSend("/users/messages", tokenForUser1, message1, status().isCreated());
        performMessageSend("/users/messages", tokenForUser1, message2, status().isForbidden());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void declineFriendshipAndSendMessageTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());
        performFriendshipDecline("/users/friends/1",  tokenForUser2, status().isOk());
        performMessageSend("/users/messages", tokenForUser1, message1, status().isForbidden());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getMessageTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());

        message1 = new NewMessage(2L, "message1");
        message2 = new NewMessage(1L, "message2");
        message3 = new NewMessage(2L, "message3");
        performMessageSend("/users/messages", tokenForUser1, message1, status().isCreated());
        performMessageSend("/users/messages", tokenForUser2, message2, status().isCreated());
        performMessageSend("/users/messages", tokenForUser1, message3, status().isCreated());

        responseMessage2 = new ResponseMessage(2L, "message2", new UserShortDto(2L, "Alex"), new UserShortDto(1L, "Mik"), LocalDateTime.now());

        performGetMessage("/users/messages/2", tokenForUser1, responseMessage2,
                status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getMessagesMainTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());

        message1 = new NewMessage(2L, "message1");
        message2 = new NewMessage(1L, "message2");
        message3 = new NewMessage(2L, "message3");
        performMessageSend("/users/messages", tokenForUser1, message1, status().isCreated());
        performMessageSend("/users/messages", tokenForUser2, message2, status().isCreated());
        performMessageSend("/users/messages", tokenForUser1, message3, status().isCreated());

        responseMessage1 = new ResponseMessage(1L, "message1", new UserShortDto(1L, "Mik"), new UserShortDto(2L, "Alex"), LocalDateTime.now());
        responseMessage2 = new ResponseMessage(2L, "message2", new UserShortDto(2L, "Alex"), new UserShortDto(1L, "Mik"), LocalDateTime.now());
        responseMessage3 = new ResponseMessage(3L, "message3", new UserShortDto(1L, "Mik"), new UserShortDto(2L, "Alex"), LocalDateTime.now());
        messageList = List.of(responseMessage1, responseMessage2, responseMessage3);

        performGetMessagesMain("/users/messages/main", tokenForUser1, 2L, messageList,
                status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getMessagesInTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());

        message1 = new NewMessage(2L, "message1");
        message2 = new NewMessage(1L, "message2");
        message3 = new NewMessage(2L, "message3");
        performMessageSend("/users/messages", tokenForUser1, message1, status().isCreated());
        performMessageSend("/users/messages", tokenForUser2, message2, status().isCreated());
        performMessageSend("/users/messages", tokenForUser1, message3, status().isCreated());

        responseMessage2 = new ResponseMessage(2L, "message2", new UserShortDto(2L, "Alex"), new UserShortDto(1L, "Mik"), LocalDateTime.now());
        messageList = List.of(responseMessage2);

        performGetMessagesInOut("/users/messages/income", tokenForUser1, messageList,
                status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getMessagesOutTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());
        performFriendshipRequest("/users/friends/1", tokenForUser2, friendship2, status().isOk());

        message1 = new NewMessage(2L, "message1");
        message2 = new NewMessage(1L, "message2");
        message3 = new NewMessage(2L, "message3");
        performMessageSend("/users/messages", tokenForUser1, message1, status().isCreated());
        performMessageSend("/users/messages", tokenForUser2, message2, status().isCreated());
        performMessageSend("/users/messages", tokenForUser1, message3, status().isCreated());

        responseMessage1 = new ResponseMessage(1L, "message1", new UserShortDto(1L, "Mik"), new UserShortDto(2L, "Alex"), LocalDateTime.now());
        responseMessage3 = new ResponseMessage(3L, "message3", new UserShortDto(1L, "Mik"), new UserShortDto(2L, "Alex"), LocalDateTime.now());
        messageList = List.of(responseMessage1, responseMessage3);

        performGetMessagesInOut("/users/messages/outgoing", tokenForUser1, messageList,
                status().isOk());
    }

    private String registerUserAndGetToken(RegistrationRequest registrationRequest) throws Exception {
        mockMvc.perform(post("/registration")
                        .content(objectMapper.writeValueAsString(registrationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        return getTokenForLogin(registrationRequest.getEmail(), registrationRequest.getPassword(), mockMvc);
    }

    private void performFriendshipRequest(String friendshipUrl, String token, Object expectedContent,
                                          ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(put(friendshipUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(expectedContent)));
    }

    private void performFriendshipDecline(String friendshipUrl, String token,
                                          ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(delete(friendshipUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }

    private void performMessageSend(String messageSendUrl, String token, NewMessage newMessage,
                                    ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(post(messageSendUrl)
                        .content(objectMapper.writeValueAsString(newMessage))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }

    private void performGetMessagesMain(String messagesUrl, String token, Long friendId, List<ResponseMessage> messages,
                                   ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(messagesUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("friendId", friendId.toString())
                        .param("from", "0")
                        .param("size", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(messages)));
    }

    private void performGetMessagesInOut(String messagesUrl, String token, List<ResponseMessage> messages,
                                        ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(messagesUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("from", "0")
                        .param("size", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(messages)));
    }

    private void performGetMessage(String messageUrl, String token, ResponseMessage message,
                                         ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(messageUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(message)));
    }
}
