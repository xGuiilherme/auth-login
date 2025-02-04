package com.example.authentication.security.steps;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.example.authentication.security.CustomUserDetailsService;
import com.example.authentication.security.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserDetailsValidationStep implements AuthenticationStep {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public UserDetailsValidationStep(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean process(String jwt, HttpServletRequest request) {
        String userEmail = jwtService.extractUsername(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(jwt, userDetails)) {
            return false;
        }
        setAuthenticationContext(userDetails, request);
        return true;
    }

    private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
