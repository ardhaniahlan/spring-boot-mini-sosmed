package com.devdan.minisosmed.service;

import com.devdan.minisosmed.config.TokenBlacklist;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.model.request.LoginUserRequest;
import com.devdan.minisosmed.model.response.TokenResponse;
import com.devdan.minisosmed.repository.UserRepository;
import com.devdan.minisosmed.resolver.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findByUsername(request.getIdentifier())
                .orElseGet(() -> userRepository.findByEmail(request.getIdentifier())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credential Wrong");
        }

        String token = jwtUtil.generatedToken(user);
        Long expiredAt = jwtUtil.getExpirationTime(token);

        return TokenResponse.builder()
                .token(token)
                .expiredAt(expiredAt)
                .build();
    }

    @Transactional
    public void logout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Missing");
        }
        tokenBlacklist.add(authHeader);
    }
}
