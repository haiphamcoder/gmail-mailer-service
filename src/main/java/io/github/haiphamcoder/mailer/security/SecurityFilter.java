package io.github.haiphamcoder.mailer.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.haiphamcoder.mailer.dto.ApiCommonResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Security filter that verifies HMAC signatures for API requests.
 * <p>
 * This filter intercepts all requests to protected endpoints and validates:
 * <ul>
 *   <li>Presence of required security headers (X-Access-Key, X-Timestamp, X-Access-Sign)</li>
 *   <li>HMAC signature verification using the provided timestamp and project token</li>
 *   <li>Timestamp validity to prevent replay attacks</li>
 * </ul>
 * <p>
 * The filter only applies to API endpoints (paths starting with /api/) and can be
 * disabled via configuration for development/testing purposes.
 * <p>
 * Security headers expected:
 * - X-Access-Key: The access key (currently not used for validation, but logged)
 * - X-Timestamp: Unix timestamp in milliseconds
 * - X-Access-Sign: HMAC-SHA512 signature of (timestamp)
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private static final String API_PATH_PREFIX = "/api/";
    private static final String ACCESS_KEY_HEADER = "X-Access-Key";
    private static final String TIMESTAMP_HEADER = "X-Timestamp";
    private static final String SIGNATURE_HEADER = "X-Access-Sign";

    private final HmacSignatureService hmacService;
    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Skip security check if disabled or not an API request
        if (!securityProperties.isEnabled() || !isApiRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract security headers
            String accessKey = request.getHeader(ACCESS_KEY_HEADER);
            String timestampStr = request.getHeader(TIMESTAMP_HEADER);
            String signature = request.getHeader(SIGNATURE_HEADER);

            // Validate required headers
            if (accessKey == null || accessKey.isBlank()) {
                handleSecurityError(response, "MISSING_ACCESS_KEY", "Missing X-Access-Key header");
                return;
            }

            if (timestampStr == null || timestampStr.isBlank()) {
                handleSecurityError(response, "MISSING_TIMESTAMP", "Missing X-Timestamp header");
                return;
            }

            if (signature == null || signature.isBlank()) {
                handleSecurityError(response, "MISSING_SIGNATURE", "Missing X-Access-Sign header");
                return;
            }


            // Parse and validate timestamp
            long timestamp;
            try {
                timestamp = Long.parseLong(timestampStr);
            } catch (NumberFormatException e) {
                handleSecurityError(response, "INVALID_TIMESTAMP", "Invalid timestamp format");
                return;
            }

            // Check timestamp validity
            if (!hmacService.isTimestampValid(timestamp, securityProperties.getTimestampTolerance())) {
                handleSecurityError(response, "INVALID_TIMESTAMP", "Request timestamp is too old or invalid");
                return;
            }

            // Verify HMAC signature
            boolean isValidSignature = hmacService.verifySignature(
                timestamp, 
                securityProperties.getSecretKey(), 
                signature
            );

            if (!isValidSignature) {
                if (securityProperties.isLogSecurityEvents()) {
                    log.warn("Invalid HMAC signature for access key: {}, IP: {}", 
                        accessKey, getClientIpAddress(request));
                }
                handleSecurityError(response, "INVALID_SIGNATURE", "Invalid signature");
                return;
            }

            // Security validation passed, continue with request
            if (securityProperties.isLogSecurityEvents()) {
                log.debug("Valid HMAC signature for access key: {}, IP: {}", 
                    accessKey, getClientIpAddress(request));
            }
            
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("Security filter error", e);
            handleSecurityError(response, "SECURITY_ERROR", "Security validation failed");
        }
    }

    /**
     * Checks if the request is for an API endpoint that requires security validation.
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith(API_PATH_PREFIX);
    }

    /**
     * Handles security validation errors by returning appropriate HTTP responses.
     */
    private void handleSecurityError(HttpServletResponse response, String errorCode, String errorMessage) 
            throws IOException {
        
        if (securityProperties.isLogSecurityEvents()) {
            log.warn("Security validation failed: {} - {}", errorCode, errorMessage);
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String responseBody = objectMapper.writeValueAsString(
            ApiCommonResponse.error(errorCode, 
                securityProperties.isDetailedErrorMessages() ? errorMessage : "Authentication failed")
        );

        response.getWriter().write(responseBody);
    }

    /**
     * Extracts the client IP address from the request, considering proxy headers.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
