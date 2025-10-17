package io.github.haiphamcoder.mailer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.haiphamcoder.mailer.dto.ApiCommonResponse;
import io.github.haiphamcoder.mailer.dto.EmailRequest;
import io.github.haiphamcoder.mailer.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<ApiCommonResponse<String>> sendEmail(
            @Valid @RequestBody EmailRequest request) {
        String id = emailService.sendEmail(request);
        return ResponseEntity.ok(ApiCommonResponse.success(id));
    }

}
