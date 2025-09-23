package com.devdan.minisosmed.resolver;

import com.devdan.minisosmed.config.TokenBlacklist;
import com.devdan.minisosmed.entity.User;
import com.devdan.minisosmed.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class JwtArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();
        String token = httpServletRequest.getHeader("Authorization");

        if (token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Missing");
        }

        if (tokenBlacklist.contains(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        Claims claims = jwtUtil.validateAndGetClaims(token);
        String username = claims.get("username", String.class);
        String email = claims.get("email", String.class);

        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")));
    }
}
