package com.example.authentication.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.authentication.exceptions.ExceptionMessages;
import com.example.authentication.security.CustomUserDetailsService;
import com.example.authentication.security.JwtAuthenticationFilter;
import com.example.authentication.security.JwtService;
import com.example.authentication.security.TokenValidator;
import com.example.authentication.security.steps.AuthenticationStep;
import com.example.authentication.security.steps.JwtValidationStep;
import com.example.authentication.security.steps.UserDetailsValidationStep;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final TokenValidator tokenValidator;
    private final List<AuthenticationStep> authenticationSteps;

    public SecurityConfig(AuthenticationProvider authenticationProvider, TokenValidator tokenValidator, List<AuthenticationStep> authenticationSteps) {
        this.authenticationProvider = authenticationProvider;
        this.tokenValidator = tokenValidator;
        this.authenticationSteps = authenticationSteps;
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenValidator, authenticationSteps);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger*/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/auth/singup", "/api/v1/auth/login", "/api/v1/auth/reset-password").permitAll()
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.disable())
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ExceptionMessages.UNAUTHORIZED);
                        })
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    List<AuthenticationStep> authenticationSteps(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        return List.of(
                new JwtValidationStep(jwtService),
                new UserDetailsValidationStep(jwtService, userDetailsService)
        );
    }
}
