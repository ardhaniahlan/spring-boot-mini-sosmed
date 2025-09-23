package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.LoginUserRequest;
import com.devdan.minisosmed.model.response.TokenResponse;
import com.devdan.minisosmed.model.response.WebResponse;
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

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testLoginUsernameSuccess() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setIdentifier("test");
        request.setPassword("admin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);

        });
    }

    @Test
    void testLoginEmailSuccess() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setIdentifier("test@example.com");
        request.setPassword("admin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertNotNull(response.getData().getToken());
            assertNotNull(response.getData().getExpiredAt());

            User userDb = userRepository.findById("test").orElse(null);
            assertNotNull(userDb);

        });
    }

    @Test
    void testLoginFailedUserNotFound() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setIdentifier("test");
        request.setPassword("admin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLoginFailedWrongPassword() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setEmail("ardhan@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setName("Test");
        userRepository.save(user);

        LoginUserRequest request = new LoginUserRequest();
        request.setIdentifier("test");
        request.setPassword("idmin");

        mockMvc.perform(
                post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void logoutFailed() throws Exception{
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void logoutSuccess() throws Exception{

        User user = new User();
        user.setName("Ardhan");
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        String token = jwtUtil.generatedToken(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("OK", response.getData());

            User userDB = userRepository.findById("test").orElse(null);
            assertNotNull(userDB);
        });
    }
}