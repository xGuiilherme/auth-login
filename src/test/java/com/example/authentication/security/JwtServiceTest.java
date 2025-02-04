package com.example.authentication.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.authentication.domain.entities.User;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    public void testGenerateToken() {
        // Act
        String token = jwtService.generateToken((UserDetails) user);

        // Assert
        assertTrue(token.startsWith("Bearer "));
    }

    @Test
    public void testIsTokenValid() {
        // Arrange
        String token = jwtService.generateToken((UserDetails) user);

        // Act
        boolean isValid = jwtService.isTokenValid(token, (UserDetails) user);

        // Assert
        assertTrue(isValid);
    }
} 