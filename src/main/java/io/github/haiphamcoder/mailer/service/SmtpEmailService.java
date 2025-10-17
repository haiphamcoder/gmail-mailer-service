package io.github.haiphamcoder.mailer.service;

import java.util.UUID;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import io.github.haiphamcoder.mailer.config.MailProperties;
import io.github.haiphamcoder.mailer.dto.EmailRequest;
import io.github.haiphamcoder.mailer.util.MaskingUtil;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    @Override
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1500, multiplier = 2.0))
    public String sendEmail(EmailRequest request) {
        String messageId = UUID.randomUUID().toString();

        try {
            MimeMessage message = mailSender.createMimeMessage();

            String from = (request.from() != null && !request.from().isBlank()) ? request.from()
                    : mailProperties.getDefaultFrom();
            if (from != null && !from.isBlank()) {
                message.setFrom(new InternetAddress(from));
            }

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", request.to())));
            if (request.cc() != null && !request.cc().isEmpty()) {
                message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(String.join(",", request.cc())));
            }
            if (request.bcc() != null && !request.bcc().isEmpty()) {
                message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(String.join(",", request.bcc())));
            }
            if (request.replyTo() != null && !request.replyTo().isBlank()) {
                message.setReplyTo(InternetAddress.parse(request.replyTo()));
            } else if (mailProperties.getDefaultReplyTo() != null && !mailProperties.getDefaultReplyTo().isBlank()) {
                message.setReplyTo(InternetAddress.parse(mailProperties.getDefaultReplyTo()));
            }

            message.setSubject(request.subject(), "UTF-8");
            boolean isHtml = Boolean.TRUE.equals(request.html());
            message.setContent(request.body(), isHtml ? "text/html; charset=UTF-8" : "text/plain; charset=UTF-8");

            mailSender.send(message);
            log.info("Email sent successfully to {} with subject '{}'",
                    MaskingUtil.maskEmail(String.join(",", request.to())),
                    request.subject());

            return messageId;
        } catch (Exception e) {
            log.error("Failed to send email to {} with subject '{}': {}",
                    MaskingUtil.maskEmail(String.join(",", request.to())),
                    request.subject(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
