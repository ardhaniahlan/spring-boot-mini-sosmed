package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.Like;
import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.repository.LikeRepository;
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

import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LikeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setName("Test");
        userRepository.save(user);
    }

    @Test
    void testLikePostSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Post to like");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/likes")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", token)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertEquals("Liked", response.getData());
                    assertTrue(likeRepository.existsByUserAndPost(user, post));
                });
    }

    @Test
    void testLikeAlreadyLiked() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Post to like");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);

        Like like = new Like(UUID.randomUUID().toString(), user, post);
        likeRepository.save(like);

        mockMvc.perform(
                        post("/api/posts/" + post.getId() + "/likes")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", token)
                ).andExpect(status().isConflict())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    void testUnlikePostSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Post to like");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);

        Like like = new Like(UUID.randomUUID().toString(), user, post);
        likeRepository.save(like);

        mockMvc.perform(
                        delete("/api/posts/" + post.getId() + "/likes")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", token)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertEquals("Unliked", response.getData());
                    assertFalse(likeRepository.existsByUserAndPost(user, post));
                });
    }

    @Test
    void testUnlikeNotFound() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Post to like");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);

        mockMvc.perform(
                        delete("/api/posts/" + post.getId() + "/likes")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", token)
                ).andExpect(status().isNotFound())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    void testCountLikes() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Post to like");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(user);
        postRepository.save(post);

        likeRepository.save(new Like(UUID.randomUUID().toString(), user, post));

        mockMvc.perform(
                        get("/api/posts/" + post.getId() + "/likes/count")
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                ).andExpect(status().isOk())
                .andDo(result -> {
                    WebResponse<Long> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertTrue(response.getData() >= 1);
                });
    }

}