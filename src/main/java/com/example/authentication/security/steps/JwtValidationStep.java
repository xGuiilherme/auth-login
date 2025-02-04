package com.example.authentication.security.steps;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.authentication.exceptions.TokenExpiredException;
import com.example.authentication.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtValidationStep implements AuthenticationStep {

    private final JwtService jwtService;

    public JwtValidationStep(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean process(String jwt, HttpServletRequest request) {
        try {
            String userEmail = jwtService.extractUsername(jwt);
            return userEmail != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null;
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException();
        }
    }
}
