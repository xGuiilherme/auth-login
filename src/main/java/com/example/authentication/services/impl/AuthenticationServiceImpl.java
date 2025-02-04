package com.example.authentication.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.authentication.domain.dtos.AuthenticationDTO;
import com.example.authentication.domain.dtos.AuthenticationResponse;
import com.example.authentication.domain.dtos.UserDTO;
import com.example.authentication.domain.entities.User;
import com.example.authentication.exceptions.BusinessException;
import com.example.authentication.exceptions.InvalidCredentialsException;
import com.example.authentication.exceptions.ResourceNotFoundException;
import com.example.authentication.exceptions.TokenExpiredException;
import com.example.authentication.exceptions.UserAlreadyExistsException;
import com.example.authentication.repositories.UserRepository;
import com.example.authentication.security.JwtService;
import com.example.authentication.services.AuthenticationService;

import jakarta.transaction.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${jwt.token.expiration}")
    private long resetPasswordTokenExpiration;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationDTO authenticationDTO) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationDTO.email(),

                            authenticationDTO.password()
                    )
            );
            var user = userRepository.findByEmail(authenticationDTO.email())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            var token = jwtService.generateToken(user);
            return new AuthenticationResponse(token, "Bearer");
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Email ou senha inválidos: " + e.getMessage());
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
                    .name(userDTO.name())
                    .email(userDTO.email())
                    .password(passwordEncoder.encode(userDTO.password()))
                    .role(userDTO.role())
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
            //user.setResetPasswordTokenExpiry(LocalDateTime.now().plusHours(1));
            user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(1));
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
