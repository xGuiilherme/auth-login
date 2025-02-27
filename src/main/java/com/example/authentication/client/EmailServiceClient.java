package com.example.authentication.client;

import com.example.authentication.domain.dtos.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-service", url = "${email-service.url}")
public interface EmailServiceClient {

    @PostMapping("/api/v1/email/password-reset")
    ResponseEntity<Void> sendEmail(@RequestBody EmailRequest request);
}
