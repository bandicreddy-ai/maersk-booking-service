package com.maersk.booking.service;

import com.maersk.booking.config.AvailabilityClientProperties;
import com.maersk.booking.model.Dtos.*;
import com.maersk.booking.mongo.BookingEntity;
import com.maersk.booking.mongo.BookingRefGenerator;
import com.maersk.booking.mongo.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final WebClient availabilityClient;
    private final AvailabilityClientProperties props;
    private final BookingRepository bookingRepository;
    private final BookingRefGenerator refGenerator;

    public BookingService(WebClient availabilityWebClient,
                          AvailabilityClientProperties props,
                          BookingRepository bookingRepository,
                          BookingRefGenerator refGenerator) {
        this.availabilityClient = availabilityWebClient;
        this.props = props;
        this.bookingRepository = bookingRepository;
        this.refGenerator = refGenerator;
    }

    public Mono<AvailabilityResponse> checkAvailability(AvailabilityRequest request) {
        log.info("Calling external availability: path={} payload=[size={},type={},from={},to={},qty={}]",
                props.getPath(), request.containerSize(), request.containerType(), request.origin(), request.destination(), request.quantity());
        return availabilityClient.post()
                .uri(props.getPath())
                .bodyValue(Map.of(
                        "containerSize", request.containerSize(),
                        "containerType", request.containerType(),
                        "origin", request.origin(),
                        "destination", request.destination(),
                        "quantity", request.quantity()
                ))
                .retrieve()
                .bodyToMono(AvailablePayload.class)
                .retryWhen(Retry.backoff(props.getRetries(), Duration.ofMillis(props.getBackoffMillis())))
                .map(payload -> {
                    boolean available = payload.availableSpace() > 0;
                    log.info("External response availableSpace={} -> available={}", payload.availableSpace(), available);
                    return new AvailabilityResponse(available);
                });
    }

    public Mono<BookingResponse> createBooking(BookingRequest request) {
        log.info("Creating booking for [size={},type={},from={},to={},qty={},ts={}]",
                request.containerSize(), request.containerType(), request.origin(), request.destination(), request.quantity(), request.timestamp());
        return refGenerator.next()
                .flatMap(ref -> {
                    BookingEntity entity = new BookingEntity(
                            ref,
                            request.containerSize(),
                            request.containerType(),
                            request.origin(),
                            request.destination(),
                            request.quantity(),
                            request.timestamp()
                    );
                    return bookingRepository.save(entity)
                            .doOnSuccess(saved -> log.info("Saved booking bookingRef={}", saved.getBookingRef()))
                            .map(saved -> new BookingResponse(saved.getBookingRef()));
                })
                .doOnError(ex -> log.error("Failed to create booking: {}", ex.toString()));
    }

    private record AvailablePayload(int availableSpace) {}
}
