package com.example.authentication.services;

import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.domain.dtos.PasswordResetEmailRequest;
import com.example.authentication.domain.dtos.UserDTO;

public interface AuthenticationService {

    AuthenticationResponse authenticate(AuthenticationDTO authenticationDTO);

    AuthenticationResponse register(UserDTO userDTO);

    String requestPasswordReset(String email);

    void resetPassword(String token, String password, String passwordConfirmation);
}
