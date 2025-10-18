package io.github.haiphamcoder.mailer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.haiphamcoder.mailer.dto.ApiCommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.Instant;
import java.util.Map;

/**
 * Public API controller for endpoints that don't require authentication.
 * <p>
 * These endpoints are accessible without HMAC signature authentication
 * and are configured in the security properties as public paths.
 * <p>
 * Public paths are defined in:
 * - application.properties: api.security.public-paths
 * - SecurityProperties.publicPaths
 */
@RestController
@RequestMapping("/api/v1/public")
@Tag(name = "Public", description = "Public API endpoints (no authentication required)")
public class PublicController {

    /**
     * Health check endpoint for monitoring and load balancers.
     * <p>
     * This endpoint provides basic health information about the service
     * and can be used by monitoring systems, load balancers, or container
     * orchestration platforms to check service availability.
     *
     * @return health status information
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health check",
        description = "Returns the health status of the service. " +
                     "Used by monitoring systems and load balancers."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is healthy"),
        @ApiResponse(responseCode = "500", description = "Service is unhealthy")
    })
    public ApiCommonResponse<Map<String, Object>> health() {
        Map<String, Object> healthData = Map.of(
            "status", "UP",
            "timestamp", Instant.now(),
            "service", "gmail-mailer-service",
            "version", "1.0.0"
        );
        
        return ApiCommonResponse.success(healthData);
    }

    /**
     * Service status endpoint with detailed information.
     * <p>
     * This endpoint provides more detailed status information than the
     * health check, including service metadata and configuration status.
     *
     * @return detailed service status
     */
    @GetMapping("/status")
    @Operation(
        summary = "Service status",
        description = "Returns detailed status information about the service " +
                     "including configuration and operational details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status information retrieved successfully")
    })
    public ApiCommonResponse<Map<String, Object>> status() {
        Map<String, Object> statusData = Map.of(
            "service", "gmail-mailer-service",
            "version", "1.0.0",
            "status", "RUNNING",
            "timestamp", Instant.now(),
            "features", Map.of(
                "email_sending", true,
                "hmac_authentication", true,
                "public_apis", true
            ),
            "endpoints", Map.of(
                "email_send", "/api/v1/emails",
                "health_check", "/api/v1/public/health",
                "service_status", "/api/v1/public/status"
            )
        );
        
        return ApiCommonResponse.success(statusData);
    }

    /**
     * API information endpoint for discovery and documentation.
     * <p>
     * This endpoint provides information about available API endpoints,
     * authentication requirements, and service capabilities.
     *
     * @return API information and documentation links
     */
    @GetMapping("/info")
    @Operation(
        summary = "API information",
        description = "Returns information about the API including available " +
                     "endpoints, authentication requirements, and documentation links."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API information retrieved successfully")
    })
    public ApiCommonResponse<Map<String, Object>> info() {
        Map<String, Object> infoData = Map.of(
            "api_name", "Gmail Mailer Service API",
            "version", "v1",
            "description", "REST API for sending emails via Gmail SMTP",
            "documentation", Map.of(
                "swagger_ui", "/swagger",
                "openapi_spec", "/v3/api-docs"
            ),
            "authentication", Map.of(
                "type", "HMAC-SHA512",
                "required_headers", new String[]{
                    "X-Access-Key",
                    "X-Timestamp", 
                    "X-Access-Sign"
                },
                "public_endpoints", new String[]{
                    "/api/v1/public/health",
                    "/api/v1/public/status",
                    "/api/v1/public/info"
                }
            ),
            "endpoints", Map.of(
                "send_email", Map.of(
                    "method", "POST",
                    "path", "/api/v1/emails",
                    "auth_required", true,
                    "description", "Send an email with HMAC authentication"
                ),
                "health_check", Map.of(
                    "method", "GET",
                    "path", "/api/v1/public/health",
                    "auth_required", false,
                    "description", "Service health check"
                )
            )
        );
        
        return ApiCommonResponse.success(infoData);
    }
}
