package com.example.socialmedia.controller;

import com.example.socialmedia.dto.RegistrationRequest;
import com.example.socialmedia.dto.ResponsePost;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
class PostControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private String tokenForUser1;
    private String tokenForUser2;
    private String tokenForUser3;
    private Friendship friendship1;

    @BeforeEach
    void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        RegistrationRequest newUser1 = new RegistrationRequest("mik@mail.ru", "Mik", "password");
        RegistrationRequest newUser2 = new RegistrationRequest("alex@mail.ru", "Alex", "password2");
        RegistrationRequest newUser3 = new RegistrationRequest("oleg@mail.ru", "Oleg", "password3");
        tokenForUser1 = registerUserAndGetToken(newUser1);
        tokenForUser2 = registerUserAndGetToken(newUser2);
        tokenForUser3 = registerUserAndGetToken(newUser3);
        friendship1 = new Friendship(1L,2L, StatusFriendship.SUBSCRIBE);
        friendship1.setId(1L);
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void addPostTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile("images","image2.png",
                String.valueOf(MediaType.IMAGE_PNG), "test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        ResponsePost responsePost = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(1L, "Mik"), List.of(1L, 2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser1, "title1", "text1", files, status().isCreated(), responsePost);
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void updatePostTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile("images","image2.png",
                String.valueOf(MediaType.IMAGE_PNG), "test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        ResponsePost responsePost = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(1L, "Mik"), List.of(1L, 2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser1, "title1", "text1", files, status().isCreated(), responsePost);

        ResponsePost responsePostUpdate = new ResponsePost(1L, "title2", "text2",
                new UserShortDto(1L, "Mik"), List.of(2L), responsePost.getCreatedAt(), LocalDateTime.now());
        performUpdatePost("/posts/1", tokenForUser1,"title2", "text2", status().isOk(), responsePostUpdate);
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getPostByIdTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile("images","image2.png",
                String.valueOf(MediaType.IMAGE_PNG), "test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        ResponsePost responsePost = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(1L, "Mik"), List.of(1L, 2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser1, "title1", "text1", files, status().isCreated(), responsePost);

        performGetPost("/posts/1", tokenForUser1, responsePost, status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getImageByIdTest() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile("images","image2.png",
                String.valueOf(MediaType.IMAGE_PNG), "test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        ResponsePost responsePost = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(1L, "Mik"), List.of(1L, 2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser1, "title1", "text1", files, status().isCreated(), responsePost);

        ByteArrayResource resource = new ByteArrayResource(file1.getBytes());
        performGetImage("/posts/1/images/1", tokenForUser1, resource, status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void deletePost() throws Exception {
        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile("images","image2.png",
                String.valueOf(MediaType.IMAGE_PNG), "test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1, file2};

        ResponsePost responsePost = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(1L, "Mik"), List.of(1L, 2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser1, "title1", "text1", files, status().isCreated(), responsePost);

        performDeletePost("/posts/1", tokenForUser1, status().isOk());
    }

    @Sql("classpath:cleanup-script.sql")
    @Test
    void getPostsFromSubsTest() throws Exception {
        performFriendshipRequest("/users/friends/2", tokenForUser1, friendship1, status().isOk());

        MockMultipartFile file1 = new MockMultipartFile("images","image1.png",
                String.valueOf(MediaType.IMAGE_PNG),"test data image!".getBytes()
        );
        MockMultipartFile[] files = {file1};

        ResponsePost responsePost1 = new ResponsePost(1L, "title1", "text1",
                new UserShortDto(2L, "Alex"), List.of(1L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser2, "title1", "text1", files, status().isCreated(), responsePost1);

        ResponsePost responsePost2 = new ResponsePost(2L, "title1", "text1",
                new UserShortDto(2L, "Alex"), List.of(2L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser2, "title1", "text1", files, status().isCreated(), responsePost2);
        ResponsePost responsePost3 = new ResponsePost(3L, "title1", "text1",
                new UserShortDto(3L, "Oleg"), List.of(3L), LocalDateTime.now(), null);
        performAddPost("/posts", tokenForUser3, "title1", "text1", files, status().isCreated(), responsePost3);
        List<ResponsePost> posts = List.of(responsePost2, responsePost1);
        performGetPosts("/posts/subscriptions", tokenForUser1, posts, status().isOk());
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

    private void performAddPost(String addPostUrl, String token, String title, String text,
                                MockMultipartFile[] files, ResultMatcher expectedStatus, ResponsePost responsePost) throws Exception {

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(addPostUrl);
        MockPart part1 = new MockPart("title", title.getBytes());
        MockPart part2 = new MockPart("text", text.getBytes());

        for (MockMultipartFile file: files) {
            builder.file(file);
        }

        mockMvc.perform(builder
                        .part(part1)
                        .part(part2)
                        .header("Authorization", "Bearer " + token))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(responsePost)));
    }

    private void performUpdatePost(String updatePostUrl, String token, String title, String text, ResultMatcher expectedStatus, ResponsePost responsePost) throws Exception {

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(updatePostUrl);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        MockPart part1 = new MockPart("title", title.getBytes());
        MockPart part2 = new MockPart("text", text.getBytes());

        MockMultipartFile file1 = new MockMultipartFile("images","",
                String.valueOf(MediaType.IMAGE_PNG),"".getBytes()
        );
        MockMultipartFile[] files = {file1};
        for (MockMultipartFile file: files) {
            builder.file(file);
        }
        mockMvc.perform(builder
                        .part(part1)
                        .part(part2)
                        .param("deleteImageIds", "1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(responsePost)));
    }

    private void performGetPost(String postUrl, String token, ResponsePost post,
                                   ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(postUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(post)));
    }

    private void performDeletePost(String postUrl, String token, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(postUrl)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }


    private void performGetImage(String imageUrl, String token, ByteArrayResource resource,
                                ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(imageUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(resource.getByteArray()));
    }

    private void performGetPosts(String messagesUrl, String token, List<ResponsePost> posts,
                                         ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(get(messagesUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .param("from", "0")
                        .param("size", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus)
                .andExpect(content().json(objectMapper.writeValueAsString(posts)));
    }
}
