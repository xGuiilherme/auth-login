package com.example.authentication.exceptions;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException() {
        super("O token de autenticação expirou. Por favor, faça login novamente.");
    }
}
