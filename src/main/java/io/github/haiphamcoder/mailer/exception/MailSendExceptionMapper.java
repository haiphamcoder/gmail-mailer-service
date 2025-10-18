package io.github.haiphamcoder.mailer.exception;

import org.springframework.stereotype.Component;

import jakarta.mail.SendFailedException;

/**
 * Maps JavaMail exceptions to standardized error codes for consistent API
 * responses.
 * <p>
 * This component provides a centralized way to convert various mail-related
 * exceptions into meaningful error codes that can be returned to API clients.
 * <p>
 * The mapping strategy:
 * <ul>
 * <li>{@link SendFailedException}: Maps to "SMTP_SEND_FAILED" - indicates
 * the SMTP server rejected the message (e.g., invalid recipient, quota
 * exceeded)</li>
 * <li>Other exceptions: Maps to "SMTP_SEND_ERROR" - indicates general
 * mail sending problems (e.g., network issues, authentication failures)</li>
 * </ul>
 * <p>
 * This separation allows clients to handle different error types appropriately:
 * - SMTP_SEND_FAILED: Retry may not help, check recipient addresses
 * - SMTP_SEND_ERROR: Retry might succeed, check network/credentials
 */
@Component
public class MailSendExceptionMapper {

    /**
     * Maps a throwable to a standardized error code.
     *
     * @param throwable the exception to map
     * @return the corresponding error code
     */
    public String map(Throwable throwable) {
        if (throwable instanceof SendFailedException) {
            return "SMTP_SEND_FAILED";
        }
        return "SMTP_SEND_ERROR";
    }

}
