package com.example.authentication.controller;

import com.example.authentication.client.EmailServiceClient;
import com.example.authentication.domain.dtos.*;
import com.example.authentication.exceptions.InvalidCredentialsException;
import com.example.authentication.exceptions.TokenExpiredException;
import com.example.authentication.services.AuthenticationService;
import com.example.authentication.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final EmailServiceClient emailServiceClient;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody @Valid AuthenticationDTO dto) {
        AuthenticationResponse token = authenticationService.authenticate(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Autenticação realizada com sucesso", token));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> register(@RequestBody @Valid UserDTO dto) {
        AuthenticationResponse token = authenticationService.register(dto);
        return ResponseEntity.ok(new ApiResponse(true, "Usuário cadastrado com sucesso", token));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) {
        String token = authenticationService.requestPasswordReset(dto.email());

        EmailRequest emailRequest = new EmailRequest(dto.email(), "REDEFINIÇÃO DE SENHA", token);

        emailServiceClient.sendEmail(emailRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Email de recuperação enviado!", token));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String token, @RequestBody @Valid ResetPasswordDTO dto) {
        try {
            authenticationService.resetPassword(token, dto.newPassword(), dto.passwordConfirmation());
            return ResponseEntity.ok(new ApiResponse(true, "Senha redefinida com sucesso", null));
        } catch (InvalidCredentialsException | TokenExpiredException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Erro ao redefinir senha: " + e.getMessage(), null));
        }
    }
}
