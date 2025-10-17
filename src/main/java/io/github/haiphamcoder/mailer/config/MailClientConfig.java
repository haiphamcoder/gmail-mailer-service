package io.github.haiphamcoder.mailer.config;

import java.util.Properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.RequiredArgsConstructor;

/**
 * Mail client configuration wiring a {@link JavaMailSender} backed by
 * {@link MailProperties}. This maps hierarchical properties (host, port,
 * credentials, STARTTLS and SSL options) into the underlying JavaMail session.
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
@RequiredArgsConstructor
public class MailClientConfig {

    private final MailProperties mailProperties;

    /**
     * Builds a {@link JavaMailSender} initialized from {@link MailProperties}.
     *
     * Mapped properties:
     * - gmail.mail.host -> JavaMailSenderImpl#setHost
     * - gmail.mail.port -> JavaMailSenderImpl#setPort
     * - gmail.mail.username -> JavaMailSenderImpl#setUsername
     * - gmail.mail.password -> JavaMailSenderImpl#setPassword
     * - gmail.mail.properties.mail.smtp.auth -> mail.smtp.auth
     * - gmail.mail.properties.mail.smtp.starttls.enable ->
     * mail.smtp.starttls.enable
     * - gmail.mail.properties.mail.smtp.starttls.required ->
     * mail.smtp.starttls.required
     * - gmail.mail.properties.mail.smtp.ssl.trust -> mail.smtp.ssl.trust
     */
    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        Properties props = new Properties();

        if (mailProperties.getProperties() != null
                && mailProperties.getProperties().getMail() != null
                && mailProperties.getProperties().getMail().getSmtp() != null) {
            MailProperties.Smtp smtp = mailProperties.getProperties().getMail().getSmtp();

            if (smtp.getAuth() != null) {
                props.put("mail.smtp.auth", String.valueOf(smtp.getAuth()));
            }

            if (smtp.getStarttls() != null) {
                if (smtp.getStarttls().getEnable() != null) {
                    props.put("mail.smtp.starttls.enable", String.valueOf(smtp.getStarttls().getEnable()));
                }
                if (smtp.getStarttls().getRequired() != null) {
                    props.put("mail.smtp.starttls.required", String.valueOf(smtp.getStarttls().getRequired()));
                }
            }

            if (smtp.getSsl() != null && smtp.getSsl().getTrust() != null && !smtp.getSsl().getTrust().isBlank()) {
                props.put("mail.smtp.ssl.trust", smtp.getSsl().getTrust());
            }
        }
        sender.setJavaMailProperties(props);
        return sender;
    }

}
