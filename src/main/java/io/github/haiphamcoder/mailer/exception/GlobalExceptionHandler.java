package io.github.haiphamcoder.mailer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.github.haiphamcoder.mailer.dto.ApiCommonResponse;

/**
 * Global exception handler that provides centralized error handling for all
 * REST controllers.
 * <p>
 * This handler catches exceptions thrown anywhere in the application and
 * converts them
 * into appropriate HTTP responses with consistent error formats.
 * <p>
 * Error handling strategy:
 * <ul>
 * <li><strong>Validation errors</strong>: Returns 400 BAD_REQUEST with
 * field-specific
 * validation messages in {@link ApiCommonResponse} format</li>
 * <li><strong>API exceptions</strong>: Returns 400 BAD_REQUEST with custom
 * error codes
 * and messages in {@link ApiCommonResponse} format</li>
 * <li><strong>Unknown exceptions</strong>: Returns 500 INTERNAL_SERVER_ERROR
 * with
 * {@link ProblemDetail} format for detailed error information</li>
 * </ul>
 * <p>
 * The handler ensures that:
 * - All API responses follow a consistent structure
 * - Sensitive error details are not exposed to clients
 * - Appropriate HTTP status codes are returned
 * - Error messages are user-friendly and actionable
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from {@code @Valid} annotations on request DTOs.
     * <p>
     * Extracts the first field error and returns it in a structured format.
     * This provides clients with specific information about which field failed
     * validation and why.
     *
     * @param ex the validation exception containing field errors
     * @return 400 BAD_REQUEST with validation error details
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiCommonResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(field -> field.getField() + " " + field.getDefaultMessage())
                .orElse("Validation error");
        return ApiCommonResponse.error("VALIDATION_ERROR", message);
    }

    /**
     * Handles custom API exceptions with structured error codes.
     * <p>
     * This handler processes {@link ApiException} instances and returns their
     * error codes and messages directly to the client. This allows for
     * programmatic error handling based on specific error conditions.
     *
     * @param ex the API exception with error code and message
     * @return 400 BAD_REQUEST with the exception's error code and message
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiCommonResponse<Void> handleApi(ApiException ex) {
        return ApiCommonResponse.error(ex.code(), ex.getMessage());
    }

    /**
     * Handles security-related exceptions (authentication failures, invalid signatures, etc.).
     * <p>
     * This handler processes security exceptions that may be thrown during
     * HMAC signature verification or other security validations.
     *
     * @param ex the security exception
     * @return 401 UNAUTHORIZED with security error details
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiCommonResponse<Void> handleSecurity(SecurityException ex) {
        return ApiCommonResponse.error("SECURITY_ERROR", ex.getMessage());
    }

    /**
     * Handles all other unhandled exceptions as internal server errors.
     * <p>
     * This is a catch-all handler for any exceptions not specifically handled
     * by other methods. It returns a detailed problem response that can be
     * useful for debugging while being careful not to expose sensitive information.
     * <p>
     * In production, consider logging the full exception details while returning
     * a generic error message to the client.
     *
     * @param ex the unhandled exception
     * @return 500 INTERNAL_SERVER_ERROR with problem details
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleUnknown(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

}
