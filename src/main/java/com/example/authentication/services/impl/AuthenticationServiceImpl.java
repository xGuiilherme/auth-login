package com.example.authentication.services.impl;

import com.example.authentication.client.EmailServiceClient;
import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.domain.dtos.UserDTO;
import com.example.authentication.domain.entities.User;
import com.example.authentication.exceptions.*;
import com.example.authentication.repositories.UserRepository;
import com.example.authentication.security.JwtService;
import com.example.authentication.services.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.token.expiration}")
    private long resetPasswordTokenExpiration;

    private final EmailServiceClient emailServiceClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse authenticate(AuthenticationDTO authenticationDTO) {
        try {
            var user = userRepository.findByEmail(authenticationDTO.email())
                    .filter(User::isEnabled)
                    .orElseThrow(() -> new BusinessException("Usuário inativo ou não cadastrado"));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationDTO.email(),authenticationDTO.password())
            );

            var token = jwtService.generateToken(user);
            return new AuthenticationResponse(token, "Bearer");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Email ou senha inválidos");
        }
    }

    @Override
    @Transactional
    public AuthenticationResponse register(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.email())) {
                throw new BusinessException("Email já cadastrado");
            }
            User user = User.builder()
                    .name(userDTO.fullName())
                    .email(userDTO.email())
                    .password(passwordEncoder.encode(userDTO.password()))
                    .role(userDTO.role())
                    .enabled(true)
                    .build();

            var savedUser = userRepository.save(user);
            var token = jwtService.generateToken(savedUser);
            return new AuthenticationResponse(token, "Bearer");
        } catch (UserAlreadyExistsException e) {
            throw new BusinessException("Email já cadastrado: " + e.getMessage());
        }
    }

    @Override
    public String requestPasswordReset(String email) {
        try {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            String token = generateResetToken();
            user.setResetPasswordToken(token);
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            return token;
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + e.getMessage());
        }
    }

    @Override
    public void resetPassword(String token, String password, String passwordConfirmation) {
        try {
            if (!password.equals(passwordConfirmation)) {
                throw new BusinessException("As senhas não coincidem");
            }

            User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Token inválido"));

            if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new TokenExpiredException("Token expirado");
            }
            user.setPassword(passwordEncoder.encode(password));
            user.setResetPasswordToken(null);
            user.setResetPasswordTokenExpiry(null);
            userRepository.save(user);
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("Token expirado: " + e.getMessage());
        }
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}
