# Gmail Mailer Service

A small Spring Boot service for sending emails via Gmail/Google Workspace SMTP with validation, retry, OpenAPI docs, and safe logging (masking).

## Features

- Validated configuration properties for Gmail SMTP (`gmail.mail.*`)
- `JavaMailSender` pre-configured with STARTTLS and SSL options
- DTO validation for email requests
- Unified API response envelope
- Exposed REST endpoint to send emails
- Swagger UI and OpenAPI docs
- Retry on transient failures
- Sensitive data masking in logs

## Requirements

- JDK 17+
- Maven 3.9+
- Gmail or Google Workspace SMTP account

## Quick Start

```bash
mvn spring-boot:run
```

Default server: `http://localhost:8080`

OpenAPI/Swagger UI: `http://localhost:8080/swagger`

## Configuration

Main properties file: `src/main/resources/application.properties`

SMTP configuration (example):

```properties
# Gmail SMTP
gmail.mail.host=smtp.gmail.com
gmail.mail.port=587
gmail.mail.username=your-account@your-domain
gmail.mail.password=your-app-password-or-password
gmail.mail.properties.mail.smtp.auth=true
gmail.mail.properties.mail.smtp.starttls.enable=true
gmail.mail.properties.mail.smtp.starttls.required=true
gmail.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

Notes:

- For Google Workspace with a custom domain, enable DKIM and ensure SPF/DMARC are correctly set to avoid spam. If you cannot edit DNS, use a verified From address matching your SMTP account and set Reply-To for responses.
- Consider using an App Password (2FA enabled) or OAuth2 if applicable.

## API

Base path: `/api/v1`

### Send Email

- Method: `POST /api/v1/emails`
- Request body (`EmailRequest`):

```json
{
  "to": ["recipient@example.com"],
  "subject": "Hello",
  "body": "<b>Hi</b>",
  "html": true,
  "cc": ["carbon@example.com"],
  "bcc": ["blind@example.com"],
  "from": "sender@your-domain",
  "replyTo": "reply@your-domain"
}
```

Validation:

- `to`: non-empty list of valid emails
- `subject`, `body`: not blank
- `html`: required boolean (defaults to false if not provided by client)
- `cc`, `bcc`: optional lists of valid emails
- `from`, `replyTo`: optional valid emails

Response (`ApiCommonResponse<T>`):

```json
{
  "success": true,
  "code": "OK",
  "message": "Success",
  "data": "<message-id>",
  "timestamp": "2025-01-01T12:00:00Z"
}
```

## Modules Overview

- `MailProperties` (`gmail.mail.*`): strongly-typed, validated SMTP settings with nested STARTTLS/SSL options
- `MailClientConfig`: creates `JavaMailSender` bean and applies JavaMail properties
- `EmailRequest`: request DTO with bean validation
- `ApiCommonResponse`: common response envelope with OpenAPI `@Schema`
- `EmailController`: REST endpoint `/api/v1/emails`
- `EmailService` / `SmtpEmailService`: send email with retry; masks sensitive logs via `MaskingUtil`
- `MaskingUtil`: masks emails and strings for safe logging
- `OpenApiConfig`: groups and describes API docs

## Running Tests

```bash
mvn test
```

## Build

```bash
mvn clean package
```

Fat JAR: `target/gmail-mailer-service-*.jar`

Run:

```bash
java -jar target/gmail-mailer-service-*.jar
```

## Security and Deliverability

- Keep credentials secure (env variables, secret managers)
- For Workspace custom domain: set up SPF, DKIM, and DMARC to improve inbox placement
- Use `Reply-To` instead of spoofing `From` across different domains

## License

Apache License 2.0
