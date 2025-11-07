package com.maersk.booking.security;

import com.maersk.booking.config.ApiKeyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ApiKeyWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(ApiKeyWebFilter.class);
    private final ApiKeyProperties props;

    public ApiKeyWebFilter(ApiKeyProperties props) { this.props = props; }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!props.isEnabled()) return chain.filter(exchange);
        String provided = exchange.getRequest().getHeaders().getFirst(props.getHeader());
        if (provided == null || !provided.equals(props.getValue())) {
            log.warn("Unauthorized request: missing/invalid API key");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
