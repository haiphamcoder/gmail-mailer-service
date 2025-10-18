package io.github.haiphamcoder.mailer.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.haiphamcoder.mailer.dto.ApiCommonResponse;
import io.github.haiphamcoder.mailer.dto.EmailRequest;
import io.github.haiphamcoder.mailer.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * REST controller for email operations.
 * <p>
 * This controller provides endpoints for sending emails via SMTP with HMAC signature authentication.
 * All endpoints require proper security headers for authentication.
 * <p>
 * Required security headers:
 * - X-Access-Key: Access key for the API
 * - X-Timestamp: Unix timestamp in milliseconds
 * - X-Project-Token: Project token for signature generation
 * - X-Access-Sign: HMAC-SHA512 signature of (timestamp + project_token)
 */
@RestController
@RequestMapping("/api/v1/emails")
@RequiredArgsConstructor
@Tag(name = "Email", description = "Email sending operations")
@SecurityRequirement(name = "HMAC-SHA512")
public class EmailController {

    private final EmailService emailService;

    /**
     * Sends an email with the provided request data.
     * <p>
     * This endpoint requires HMAC signature authentication via security headers.
     * The request body is validated according to EmailRequest constraints.
     * <p>
     * Security requirements:
     * - Valid HMAC signature in X-Access-Sign header
     * - Timestamp within tolerance window (X-Timestamp header)
     * - Valid project token (X-Project-Token header)
     *
     * @param accessKey the access key (logged for audit purposes)
     * @param timestamp the request timestamp
     * @param projectToken the project token
     * @param signature the HMAC signature
     * @param request the email request with recipient, subject, body, etc.
     * @return response containing the message ID if successful
     */
    @PostMapping
    @Operation(
        summary = "Send email",
        description = "Sends an email with the provided recipient, subject, and body. " +
                     "Requires HMAC signature authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or validation error"),
        @ApiResponse(responseCode = "401", description = "Authentication failed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiCommonResponse<String>> sendEmail(
            @Parameter(description = "Access key for API authentication", required = true)
            @RequestHeader("X-Access-Key") String accessKey,
            
            @Parameter(description = "Request timestamp in milliseconds", required = true)
            @RequestHeader("X-Timestamp") String timestamp,
            
            @Parameter(description = "Project token for signature generation", required = true)
            @RequestHeader("X-Project-Token") String projectToken,
            
            @Parameter(description = "HMAC-SHA512 signature", required = true)
            @RequestHeader("X-Access-Sign") String signature,
            
            @Valid @RequestBody EmailRequest request) {
        
        String messageId = emailService.sendEmail(request);
        return ResponseEntity.ok(ApiCommonResponse.success(messageId));
    }

}
