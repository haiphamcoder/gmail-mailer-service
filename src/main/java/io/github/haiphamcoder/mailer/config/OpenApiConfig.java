package io.github.haiphamcoder.mailer.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("mailer")
                .addOpenApiCustomizer(openApi -> openApi.setInfo(new Info()
                        .title("Gmail Mailer Service API")
                        .version("v1")
                        .description("REST API for sending emails via SMTP")))
                .pathsToMatch("/api/**")
                .build();
    }

}
