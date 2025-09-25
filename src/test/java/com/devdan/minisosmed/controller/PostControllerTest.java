package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.Post;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.CreatePostRequest;
import com.devdan.minisosmed.model.request.UpdatePostRequest;
import com.devdan.minisosmed.model.response.PostResponse;
import com.devdan.minisosmed.model.response.WebResponse;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
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
    void testCreatePostSuccessPublish() throws Exception {
        User userFromDb = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(userFromDb);

        CreatePostRequest request = new CreatePostRequest();

        request.setBody("Isi post");
        request.setStatus("published");

        mockMvc.perform(
                multipart("/api/posts")
                        .file(new MockMultipartFile(
                                "imageFile",
                                "test.png",
                                "image/png",
                                "dummy image content".getBytes()
                        ))
                        .param("body", "Isi post")
                        .param("status", "published")
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getBody(), response.getData().getBody());
            assertEquals(request.getStatus(), response.getData().getStatus());

            assertTrue(postRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testCreatePostSuccessDraft() throws Exception {
        User userFromDb = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(userFromDb);

        CreatePostRequest request = new CreatePostRequest();

        request.setBody("Isi post");
        request.setStatus("draft");

        mockMvc.perform(
                multipart("/api/posts")
                        .file(new MockMultipartFile(
                                "imageFile",
                                "test.png",
                                "image/png",
                                "dummy image content".getBytes()
                        ))
                        .param("body", "Isi post")
                        .param("status", "draft")
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getBody(), response.getData().getBody());
            assertEquals(request.getStatus(), response.getData().getStatus());

            assertTrue(postRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testCreatePostSuccessWithoutBody() throws Exception {
        User userFromDb = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(userFromDb);

        CreatePostRequest request = new CreatePostRequest();
        request.setStatus("");
        request.setStatus("published");

        mockMvc.perform(
                multipart("/api/posts")
                        .file(new MockMultipartFile(
                                "imageFile",
                                "test.png",
                                "image/png",
                                "dummy image content".getBytes()
                        ))
                        .param("status", "published")
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getStatus(), response.getData().getStatus());
            assertEquals(request.getBody(), response.getData().getBody());

            assertTrue(postRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testCreatePostSuccessWithoutImage() throws Exception {
        User userFromDb = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(userFromDb);

        CreatePostRequest request = new CreatePostRequest();
        request.setBody("INI POSTING");
        request.setStatus("published");

        mockMvc.perform(
                multipart("/api/posts")
                        .param("body", "INI POSTING")
                        .param("status", "published")
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo( result -> {
            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getStatus(), response.getData().getStatus());
            assertEquals(request.getBody(), response.getData().getBody());

            assertTrue(postRepository.existsById(response.getData().getId()));
        });
    }

    @Test
    void testCreatePostFailedInvalidStatus() throws Exception {
        User userFromDb = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(userFromDb);

        mockMvc.perform(
                multipart("/api/posts")
                        .param("body", "Posting salah status")
                        .param("status", "INVALID")
                        .header("Authorization", token)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testGetAllPostPublishedSuccess() throws Exception {

        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        for (int i = 0; i < 5; i++) {
            Post post = new Post();
            post.setId(UUID.randomUUID().toString());
            post.setUser(user);
            post.setBody("Ini postingan ke-" + i);
            post.setStatus("PUBLISHED");
            post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            postRepository.save(post);
        }

        mockMvc.perform(
                get("/api/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<PostResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(5, response.getData().size());

            assertEquals(0, response.getPagingResponse().getCurrentPage());
            assertEquals(10, response.getPagingResponse().getSize());
        });
    }

    @Test
    void testGetAllPostByUserIdSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        for (int i = 0; i < 3; i++) {
            Post post = new Post();
            post.setId(UUID.randomUUID().toString());
            post.setUser(user);
            post.setBody("Postingan target user ke-" + i);
            post.setStatus("PUBLISHED");
            post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            postRepository.save(post);
        }

        mockMvc.perform(
                get("/api/users/" + user.getId() + "/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<PostResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(3, response.getData().size());

            response.getData().forEach(post -> assertEquals("PUBLISHED", post.getStatus()));
        });
    }


    @Test
    void testGetAllMyPostsSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        for (int i = 0; i < 2; i++) {
            Post post = new Post();
            post.setId(UUID.randomUUID().toString());
            post.setUser(user);
            post.setBody("Published post ke-" + i);
            post.setStatus("PUBLISHED");
            post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
            postRepository.save(post);
        }

        Post draft = new Post();
        draft.setId(UUID.randomUUID().toString());
        draft.setUser(user);
        draft.setBody("Draft pribadi");
        draft.setStatus("DRAFT");
        draft.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        postRepository.save(draft);

        mockMvc.perform(
                get("/api/users/me/posts")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<PostResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(3, response.getData().size());
        });

        mockMvc.perform(
                get("/api/users/me/posts")
                        .param("status", "PUBLISHED")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<PostResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(2, response.getData().size());
            response.getData().forEach(post -> assertEquals("PUBLISHED", post.getStatus()));
        });

        mockMvc.perform(
                get("/api/users/me/posts")
                        .param("status", "DRAFT")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<PostResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {}
            );

            assertNull(response.getErrors());
            assertEquals(1, response.getData().size());
            assertEquals("DRAFT", response.getData().get(0).getStatus());
        });
    }


    @Test
    void testDeletePostNotFound() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        mockMvc.perform(
                delete("/api/posts/13424")
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
    void testDeletePostSuccess() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setUser(user);
        post.setBody("Published post ke-");
        post.setStatus("PUBLISHED");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        postRepository.save(post);

        mockMvc.perform(
                delete("/api/posts/" + post.getId())
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
        });
    }

    @Test
    void testDeletePostNotCurrentUser() throws Exception {
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        User notUserCurrent = new User();
        notUserCurrent.setId(UUID.randomUUID().toString());
        notUserCurrent.setUsername("haha");
        notUserCurrent.setEmail("test2@example.com");
        notUserCurrent.setPassword(passwordEncoder.encode("admin"));
        notUserCurrent.setName("haha");
        userRepository.save(notUserCurrent);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Haiiiii");
        post.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        post.setUser(notUserCurrent);
        postRepository.save(post);

        mockMvc.perform(
                        delete("/api/posts/" + post.getId())
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .header("Authorization", token)
                ).andExpect(status().isForbidden())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<>() {}
                    );
                    assertNotNull(response.getErrors());
                    assertTrue(postRepository.existsById(post.getId()));
                });
    }


    @Test
    void testUpdatePostSuccess() throws Exception{
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        Post post = new Post();
        post.setId(UUID.randomUUID().toString());
        post.setBody("Published post ke-");
        post.setUser(user);
        postRepository.save(post);

        UpdatePostRequest request = new UpdatePostRequest();
        request.setBody("Uhuy");

        mockMvc.perform(
                patch("/api/posts/" + post.getId())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<PostResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getBody(), response.getData().getBody());

            assertTrue(postRepository.existsById(post.getId()));
        });
    }

    @Test
    void testUpdatePostNotFound() throws Exception{
        User user = userRepository.findByUsername("test").orElseThrow();
        String token = jwtUtil.generatedToken(user);

        UpdatePostRequest request = new UpdatePostRequest();
        request.setBody("Uhuy");

        mockMvc.perform(
                patch("/api/posts/324234")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", token)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }
}