package com.maersk.booking.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("bookings")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.info(new Info().title("Maersk Booking API").version("v1"));
                    openApi.addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"));
                    SecurityScheme apiKey = new SecurityScheme()
                            .type(SecurityScheme.Type.APIKEY)
                            .in(SecurityScheme.In.HEADER)
                            .name("X-API-KEY");
                    openApi.getComponents().addSecuritySchemes("ApiKeyAuth", apiKey);
                })
                .build();
    }
}
