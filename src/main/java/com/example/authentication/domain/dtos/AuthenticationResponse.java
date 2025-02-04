package com.example.authentication.domain.dtos;

public record AuthenticationResponse(
    String token,
    String type
) {
}
