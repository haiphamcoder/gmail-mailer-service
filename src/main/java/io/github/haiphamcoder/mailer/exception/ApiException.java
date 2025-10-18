package io.github.haiphamcoder.mailer.exception;

/**
 * Custom runtime exception for API-specific errors with structured error codes.
 * <p>
 * This exception is designed to carry both a machine-readable error code and
 * a human-readable message, making it suitable for API error responses.
 * <p>
 * The error code can be used by clients to programmatically handle specific
 * error conditions, while the message provides context for debugging.
 * <p>
 * Example usage:
 * 
 * <pre>{@code
 * throw new ApiException("INVALID_EMAIL_FORMAT", "Email address is malformed");
 * throw new ApiException("RATE_LIMIT_EXCEEDED", "Too many requests");
 * }</pre>
 */
public class ApiException extends RuntimeException {

    /** Machine-readable error code for programmatic handling */
    private final String code;

    /**
     * Creates a new API exception with the specified error code and message.
     *
     * @param code    the error code (should be UPPER_SNAKE_CASE for consistency)
     * @param message the human-readable error message
     */
    public ApiException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String code() {
        return code;
    }

}
