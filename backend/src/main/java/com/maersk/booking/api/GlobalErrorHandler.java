package com.maersk.booking.api;

import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(RuntimeException.class)
    public Mono<Void> handle(ServerWebExchange exchange, RuntimeException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String json = "{\"message\":\"Sorry, there was a problem processing your request\"}";
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        return exchange.getResponse().writeWith(Mono.just(bufferFactory.wrap(bytes)));
    }
}
