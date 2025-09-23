package com.devdan.minisosmed.controller;

import com.devdan.minisosmed.model.request.LoginUserRequest;
import com.devdan.minisosmed.model.response.TokenResponse;
import com.devdan.minisosmed.model.response.WebResponse;
import com.devdan.minisosmed.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        TokenResponse response = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(HttpServletRequest request){
        authService.logout(request);
        return WebResponse.<String>builder().data("OK").build();
    }

}
