package io.github.haiphamcoder.mailer.service;

import io.github.haiphamcoder.mailer.dto.EmailRequest;

/**
 * Service interface for sending emails.
 * Provides a method to send an email with the given request.
 */
public interface EmailService {
    /**
     * Sends an email with the given request.
     *
     * @param request the email request
     * @return the message ID of the sent email
     */
    String sendEmail(EmailRequest request);
}
