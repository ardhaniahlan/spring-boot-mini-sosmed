package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.RegisterUserRequest;
import com.devdan.minisosmed.model.request.UpdateUserRequest;
import com.devdan.minisosmed.model.response.UserResponse;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception{

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setPassword("admin");
        request.setEmail("test@example.com");
        request.setName("Test");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals("OK", response.getData());
        });

    }

    @Test
    void testRegisterFailed() throws Exception{

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("");
        request.setPassword("");
        request.setEmail("");
        request.setName("");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testRegisterDuplicated() throws Exception{

        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setName("Test");
        userRepository.save(user);

        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("test");
        request.setEmail("test@example.com");
        request.setPassword("admin");
        request.setName("Test");

        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserUnauthirizedTokenNotSend() throws Exception{
        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Test");
        user.setEmail("test@example.com");
        user.setUsername("test");
        user.setPassword(passwordEncoder.encode("admin"));

        String token = jwtUtil.generatedToken(user);

        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals("test", response.getData().getUsername());
            assertEquals("test@example.com", response.getData().getEmail());
            assertEquals("Test", response.getData().getName());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Test");
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        String expiredToken = jwtUtil.generateTokenWithExpiration("test", -1000);

        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("Authorization", expiredToken)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserUnauthirized() throws Exception{

        UpdateUserRequest request = new UpdateUserRequest();

        mockMvc.perform(
                patch("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result ->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setName("Test");
        user.setUsername("test");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(user);

        String token = jwtUtil.generatedToken(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("haha");
        request.setName("Ardhan");
        request.setPassword("admin123");

        mockMvc.perform(
                patch("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", token)
        ).andExpectAll(
                status().isOk()
        ).andDo(result ->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getErrors());
            assertEquals(request.getName(), response.getData().getName());
            assertEquals(request.getUsername(), response.getData().getUsername());

            User userDB = userRepository.findById(user.getId()).orElse(null);
            assertNotNull(userDB);
            assertTrue(passwordEncoder.matches("admin123", userDB.getPassword()));
        });
    }
}