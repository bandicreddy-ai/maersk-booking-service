package com.maersk.booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
public class ApiKeySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        // We let the WebFilter below enforce the key; security layer stays open in local/dev.
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyExchange().permitAll()
                )
                .build();
    }

    // API key guard that runs very early in the WebFlux chain
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    public WebFilter apiKeyGuard(Environment env) {
        boolean enabled = env.getProperty("security.api-key.enabled", Boolean.class, false);
        String header  = env.getProperty("security.api-key.header", "X-API-KEY");
        String value   = env.getProperty("security.api-key.value",  "dev-key");

        if (!enabled) {
            // Local/dev: do nothing, let everything pass
            return (exchange, chain) -> chain.filter(exchange);
        }

        return (exchange, chain) -> {
            String key = exchange.getRequest().getHeaders().getFirst(header);
            if (value.equals(key)) {
                return chain.filter(exchange);
            }
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        };
    }
}
