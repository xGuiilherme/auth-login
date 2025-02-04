package com.example.authentication.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authentication.exceptions.TokenExpiredException;
import com.example.authentication.security.steps.AuthenticationStep;
import com.example.authentication.utils.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenValidator tokenValidator;
    private final List<AuthenticationStep> authenticationSteps;

    public JwtAuthenticationFilter(TokenValidator tokenValidator, List<AuthenticationStep> authenticationSteps) {
        this.tokenValidator = tokenValidator;
        this.authenticationSteps = authenticationSteps;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwt = tokenValidator.extractToken(request);
        try {
            if (jwt != null) {
                for (AuthenticationStep step : authenticationSteps) {
                    if (!step.process(jwt, request)) {
                        break;
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (TokenExpiredException ex) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(
                    response.getWriter(),
                    new ApiResponse(false, ex.getMessage(), null)
            );
        }
    }
}
