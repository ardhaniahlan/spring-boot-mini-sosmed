package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.Comment;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreateCommentRequest;
import com.devdan.minisosmed.model.response.CommentResponse;
import com.devdan.minisosmed.model.response.PostResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.repository.CommentRepository;
import com.devdan.minisosmed.repository.PostRepository;
import com.devdan.minisosmed.repository.UserRepository;
import com.devdan.minisosmed.resolver.JwtUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setName("Test");
        userRepository.save(user);

        Post post = new Post();
        post.setId("ssss");
        post.setUser(user);
        post.setBody("Ini postingan ke-");
        post.setStatus("PUBLISHED");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);
    }

    @Test
    void testCreateCommentSuccess() throws Exception{
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("Ini Comment");

        mockMvc.perform(
                post("/api/posts/ssss/comments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<CommentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getBody(), response.getData().getBody());

            assertTrue(commentRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testCreateCommentBadRequest() throws Exception{
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setBody("");

        mockMvc.perform(
                post("/api/posts/ssss/comments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteCommentNotFound() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        mockMvc.perform(
                delete("/api/posts/ssss/comments/sdd")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testDeleteCommentSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = postRepository.findById("ssss").orElseThrow();

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody("Haiiiii");
        comment.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        comment.setUser(user);
        comment.setPost(post);
        commentRepository.save(comment);

        mockMvc.perform(
                delete("/api/posts/ssss/comments/" + comment.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("OK", response.getData());
            assertFalse(commentRepository.existsById(comment.getId()));
        });
    }

    @Test
    void testDeleteCommentNotCurrentUser() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = postRepository.findById("ssss").orElseThrow();

        User notUserCurrent = new User();
        notUserCurrent.setId(UUID.randomUUID().toString());
        notUserCurrent.setUsername("haha");
        notUserCurrent.setEmail("test2@example.com");
        notUserCurrent.setPassword(passwordEncoder.encode("admin"));
        notUserCurrent.setName("haha");
        userRepository.save(notUserCurrent);

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID().toString());
        comment.setBody("Haiiiii");
        comment.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        comment.setUser(notUserCurrent);
        comment.setPost(post);
        commentRepository.save(comment);

        mockMvc.perform(
                        delete("/api/posts/ssss/comments/" + comment.getId())
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization",  token)
                ).andExpect(status().isForbidden())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getErrors());
                    assertTrue(commentRepository.existsById(comment.getId()));
                });
    }

    @Test
    void testGetAllCommenttByPostIdSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = postRepository.findById("ssss").orElseThrow();

        for (int i = 0; i < 3; i++) {
            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setBody("Haiiiii" + i);
            comment.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            comment.setUser(user);
            comment.setPost(post);
            commentRepository.save(comment);
        }

        mockMvc.perform(
                get("/api/posts/" + post.getId() + "/comments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<CommentResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(3, response.getData().size());
        });
    }

}