package com.hhconcert.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI concertApi() {
        return new OpenAPI()
                .components(components())
                .info(info());
    }

    private Components components() {
        SecurityScheme securityScheme = new SecurityScheme()
                .name("queue-token")
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("uuid");

        return new Components().addSecuritySchemes("queue-token", securityScheme);
    }

    private Info info() {
        return new Info()
                .title("Concert Reservation API")
                .version("0.1")
                .description("콘서트 예약 서비스");
    }
}
