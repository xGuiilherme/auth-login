package com.example.authentication.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordDTO(
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$&*])(?=.*[0-9])(?=.*[a-z]).{8,}$", 
            message = "A senha deve conter pelo menos 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial")
    String newPassword,

    @NotBlank(message = "A confirmação de senha é obrigatória")
    String passwordConfirmation
) {}
