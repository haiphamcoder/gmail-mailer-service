package io.github.haiphamcoder.mailer.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for API security settings.
 * <p>
 * These properties control HMAC signature verification and other security features.
 * <p>
 * Example configuration:
 * <pre>
 * api.security.enabled=true
 * api.security.secret-key=${API_SECRET_KEY:your-secret-key}
 * api.security.timestamp-tolerance=300
 * </pre>
 * <p>
 * Security considerations:
 * - Store secret-key in environment variables or secret managers
 * - Use strong, randomly generated keys (at least 32 characters)
 * - Regularly rotate secret keys in production
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "api.security")
public class SecurityProperties {

    /**
     * Whether API security is enabled.
     * When disabled, HMAC signature verification is skipped.
     */
    private boolean enabled = true;

    /**
     * The secret key used for HMAC signature generation and verification.
     * Should be stored securely (environment variables, secret managers).
     * Must be at least 32 characters for security.
     */
    @NotBlank(message = "Secret key is required when security is enabled")
    private String secretKey;


    /**
     * Timestamp tolerance in seconds for replay attack prevention.
     * Requests with timestamps older than this value will be rejected.
     * Default: 300 seconds (5 minutes).
     */
    @NotNull
    @Positive(message = "Timestamp tolerance must be positive")
    private Long timestampTolerance = 300L;

    /**
     * Whether to log security events (failed authentications, etc.).
     * Useful for monitoring and debugging.
     */
    private boolean logSecurityEvents = true;

    /**
     * Whether to include detailed error messages in security failures.
     * When false, generic error messages are returned to prevent information leakage.
     */
    private boolean detailedErrorMessages = false;

    /**
     * List of API paths that should be excluded from HMAC authentication.
     * These paths will be accessible without security headers.
     * <p>
     * Examples:
     * - "/api/v1/public/**" - all public APIs
     * - "/api/v1/health" - health check endpoint
     * - "/api/v1/status" - status endpoint
     * <p>
     * Path patterns support Ant-style wildcards:
     * - "*" matches any character except path separator
     * - "**" matches any characters including path separators
     */
    private String[] publicPaths = {
        "/api/v1/public/**",
        "/api/v1/health",
        "/api/v1/status"
    };
}
