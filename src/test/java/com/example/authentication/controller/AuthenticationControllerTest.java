package com.example.authentication.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.exceptions.InvalidCredentialsException;
import com.example.authentication.services.AuthenticationService;
import com.example.authentication.utils.ApiResponse;

public class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticate_Success() {
        // Arrange
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "password");
        String token = "mockToken";
        when(authenticationService.authenticate(authDTO)).thenReturn(new AuthenticationResponse(token, "Bearer"));

        // Act
        ResponseEntity<ApiResponse> response = authenticationController.authenticate(authDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Autenticação realizada com sucesso", response.getBody().message());
        assertEquals(token, response.getBody().data());
    }

    @Test
    public void testAuthenticate_InvalidCredentials() {
        // Arrange
        AuthenticationDTO authDTO = new AuthenticationDTO("test@example.com", "wrongPassword");
        when(authenticationService.authenticate(authDTO)).thenThrow(new InvalidCredentialsException("Credenciais inválidas"));

        // Act
        ResponseEntity<ApiResponse> response = authenticationController.authenticate(authDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro ao redefinir senha: Credenciais inválidas", response.getBody().message());
    }
}