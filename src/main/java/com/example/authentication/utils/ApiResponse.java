package com.example.authentication.utils;

public record ApiResponse(
        boolean success,
        String message,
        Object data
) {}
