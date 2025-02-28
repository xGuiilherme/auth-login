package com.example.authentication.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email
) {}
