package com.example.authentication.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.domain.entities.User;
import com.example.authentication.exceptions.InvalidCredentialsException;
import com.example.authentication.repositories.UserRepository;
import com.example.authentication.security.JwtService;

public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
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
    public void testAuthenticate_Success() {
        // Arrange
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("mockToken");

        // Act
        AuthenticationResponse response = authenticationService.authenticate(authDTO);

        // Assert
        assertEquals("mockToken", response.token());
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Arrange
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "wrongPassword");
        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));

        // Act & Assert
        InvalidCredentialsException exception = 
            assertThrows(InvalidCredentialsException.class, () -> authenticationService.authenticate(authDTO));
        assertEquals("Credenciais inválidas", exception.getMessage());
    }
} 