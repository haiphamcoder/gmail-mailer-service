package io.github.haiphamcoder.mailer.dto;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard API envelope used across endpoints.
 * Includes success flag, application code, message, payload and timestamp.
 */
@Schema(name = "ApiCommonResponse", description = "Standard API envelope for all responses")
public record ApiCommonResponse<T>(
        @Schema(description = "Operation success flag", example = "true") boolean success,
        @Schema(description = "Application status code or 'OK'", example = "OK") String code,
        @Schema(description = "Human-readable message", example = "Success") String message,
        @Schema(description = "Response payload; type varies by endpoint") T data,
        @Schema(description = "Response timestamp", type = "string", format = "date-time") Instant timestamp) {

    public static <T> ApiCommonResponse<T> success(T data) {
        return new ApiCommonResponse<>(true, "OK", "Success", data, Instant.now());
    }

    public static <T> ApiCommonResponse<T> error(String code, String message) {
        return new ApiCommonResponse<>(false, code, message, null, Instant.now());
    }

}
