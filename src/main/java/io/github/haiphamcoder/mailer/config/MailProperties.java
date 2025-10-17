package io.github.haiphamcoder.mailer.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Gmail SMTP client.
 * <p>
 * Prefix: {@code gmail.mail}
 * <p>
 * Example keys:
 * <ul>
 * <li>{@code gmail.mail.host}</li>
 * <li>{@code gmail.mail.port}</li>
 * <li>{@code gmail.mail.username}</li>
 * <li>{@code gmail.mail.password}</li>
 * <li>{@code gmail.mail.properties.mail.smtp.auth}</li>
 * <li>{@code gmail.mail.properties.mail.smtp.starttls.enable}</li>
 * <li>{@code gmail.mail.properties.mail.smtp.starttls.required}</li>
 * <li>{@code gmail.mail.properties.mail.smtp.ssl.trust}</li>
 * </ul>
 * Values are validated at startup; the application will fail fast if required
 * fields are missing or invalid.
 */
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "gmail.mail")
public class MailProperties {

    /** SMTP server hostname. Maps to {@code gmail.mail.host}. */
    @NotBlank
    private String host;

    /** SMTP server port (1-65535). Maps to {@code gmail.mail.port}. */
    @NotNull
    @Min(1)
    @Max(65535)
    private Integer port;

    /** Username for SMTP authentication. Maps to {@code gmail.mail.username}. */
    @NotBlank
    private String username;

    /** Password or app-specific password. Maps to {@code gmail.mail.password}. */
    @NotBlank
    private String password;

    @Valid
    @NestedConfigurationProperty
    private final Properties properties = new Properties();

    /** Default from email address. Maps to {@code gmail.mail.defaultFrom}. */
    private String defaultFrom;

    /**
     * Default reply-to email address. Maps to {@code gmail.mail.defaultReplyTo}.
     */
    private String defaultReplyTo;

    @Getter
    @Setter
    @Validated
    /**
     * Container for JavaMail-style nested properties under
     * {@code gmail.mail.properties}.
     */
    public static class Properties {

        @Valid
        @NestedConfigurationProperty
        private final Mail mail = new Mail();

    }

    @Getter
    @Setter
    @Validated
    /** Container for {@code gmail.mail.properties.mail.*}. */
    public static class Mail {

        @Valid
        @NestedConfigurationProperty
        private final Smtp smtp = new Smtp();

    }

    @Getter
    @Setter
    @Validated
    /** Settings under {@code gmail.mail.properties.mail.smtp.*}. */
    public static class Smtp {

        @NotNull
        /** Enable SMTP auth. Maps to {@code gmail.mail.properties.mail.smtp.auth}. */
        private Boolean auth;

        @Valid
        @NestedConfigurationProperty
        private final Starttls starttls = new Starttls();

        @Valid
        @NestedConfigurationProperty
        private final Ssl ssl = new Ssl();

        @Getter
        @Setter
        @Validated
        /** STARTTLS related flags under {@code ...smtp.starttls.*}. */
        public static class Starttls {
            @NotNull
            /** Maps to {@code gmail.mail.properties.mail.smtp.starttls.enable}. */
            private Boolean enable;

            @NotNull
            /** Maps to {@code gmail.mail.properties.mail.smtp.starttls.required}. */
            private Boolean required;
        }

        @Getter
        @Setter
        @Validated
        /** SSL related settings under {@code ...smtp.ssl.*}. */
        public static class Ssl {
            @NotBlank
            /** Maps to {@code gmail.mail.properties.mail.smtp.ssl.trust}. */
            private String trust;
        }

    }
}
