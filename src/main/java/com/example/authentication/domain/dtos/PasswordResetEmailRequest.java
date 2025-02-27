package com.example.authentication.domain.dtos;

public record PasswordResetEmailRequest(
        String userEmail,
        String resetToken
) {}
