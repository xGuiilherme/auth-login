package com.example.authentication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.domain.dtos.ForgotPasswordDTO;
import com.example.authentication.domain.dtos.ResetPasswordDTO;
import com.example.authentication.domain.dtos.UserDTO;
import com.example.authentication.exceptions.InvalidCredentialsException;
import com.example.authentication.exceptions.TokenExpiredException;
import com.example.authentication.services.AuthenticationService;
import com.example.authentication.utils.ApiResponse;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
        AuthenticationResponse token = authenticationService.authenticate(authenticationDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Autenticação realizada com sucesso", token));
    }

    @PostMapping("/singup")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserDTO userDTO) {
        AuthenticationResponse token = authenticationService.register(userDTO);
        return ResponseEntity.ok(new ApiResponse(true, "Usuário cadastrado com sucesso", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        String token = authenticationService.requestPasswordReset(forgotPasswordDTO.email());
        return ResponseEntity.ok(new ApiResponse(true, "Token de redefinição de senha gerado com sucesso", token));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        try {
            authenticationService.resetPassword(token, resetPasswordDTO.newPassword(), resetPasswordDTO.passwordConfirmation());
            return ResponseEntity.ok(new ApiResponse(true, "Senha redefinida com sucesso", null));
        } catch (InvalidCredentialsException | TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Erro ao redefinir senha: " + e.getMessage(), null));
        }
    }
}
