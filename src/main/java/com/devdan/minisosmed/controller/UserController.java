package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.RegisterUserRequest;
import com.devdan.minisosmed.model.request.UpdateUserRequest;
import com.devdan.minisosmed.model.response.UserResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "/api/users/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/users/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(User user){
        UserResponse response = userService.get(user);
        return WebResponse.<UserResponse>builder().data(response).build();
    }

    @PatchMapping(
            path = "/api/users/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request){
        UserResponse response = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(response).build();
    }

}
