package com.example.authentication.domain.dtos;

public record EmailRequest(
        String to,
        String subject,
        String token
) {
}
