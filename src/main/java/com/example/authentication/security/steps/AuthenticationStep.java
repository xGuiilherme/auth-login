package com.example.authentication.security.steps;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationStep {
    boolean process(String jwt, HttpServletRequest request);
}
