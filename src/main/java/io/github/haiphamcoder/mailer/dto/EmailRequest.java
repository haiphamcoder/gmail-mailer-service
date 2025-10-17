package io.github.haiphamcoder.mailer.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for sending an email.
 *
 * - to: required list of recipient emails
 * - subject: required
 * - body: required
 * - html: whether the body is HTML (defaults to false if null)
 * - cc/bcc: optional recipient emails
 * - from/replyTo: optional; if provided must be valid emails
 */
public record EmailRequest(
        @NotEmpty List<@NotBlank @Email String> to,
        @NotBlank String subject,
        @NotBlank String body,
        @NotNull Boolean html,
        List<@NotBlank @Email String> cc,
        List<@NotBlank @Email String> bcc,
        @Email String from,
        @Email String replyTo) {
    public EmailRequest {
        // Default html to false when null
        if (html == null) {
            html = Boolean.FALSE;
        }
    }
}
