package com.maersk.booking.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class LoggingWebFilter implements WebFilter {
    private static final Logger log = LoggerFactory.getLogger(LoggingWebFilter.class);
    private static final String CORRELATION_HEADER = "X-CORRELATION-ID";
    private static final String CORRELATION_KEY = "corrId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        String corrId = req.getHeaders().getFirst(CORRELATION_HEADER);
        if (corrId == null || corrId.isBlank()) corrId = UUID.randomUUID().toString();
        MDC.put(CORRELATION_KEY, corrId);
        long t0 = System.currentTimeMillis();
        log.info("REQ {} {} from {}", req.getMethod(), req.getURI().getPath(),
                req.getRemoteAddress() != null ? req.getRemoteAddress().getAddress() : "unknown");

        return chain.filter(exchange).doOnSuccess(v -> {
            long ms = System.currentTimeMillis() - t0;
            int status = exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : 200;
            log.info("RES {} {} status={} timeMs={}", req.getMethod(), req.getURI().getPath(), status, ms);
        }).doOnError(ex -> {
            long ms = System.currentTimeMillis() - t0;
            log.error("ERR {} {} after {} ms: {}", req.getMethod(), req.getURI().getPath(), ms, ex.toString());
        }).doFinally(sig -> MDC.remove(CORRELATION_KEY));
    }
}
