package com.maersk.booking;

import com.github.tomakehurst.wiremock.common.Exceptions;
import com.maersk.booking.config.AvailabilityClientProperties;
import com.maersk.booking.model.Dtos.AvailabilityRequest;
import com.maersk.booking.model.Dtos.AvailabilityResponse;
import com.maersk.booking.model.Dtos.BookingRequest;
import com.maersk.booking.model.Dtos.BookingResponse;
import com.maersk.booking.mongo.BookingEntity;
import com.maersk.booking.mongo.BookingRefGenerator;
import com.maersk.booking.mongo.BookingRepository;
import com.maersk.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for BookingService using a mocked ExchangeFunction (no WireMock, no WebClient generics pain).
 */
class BookingServiceTest {

    // Low-level WebClient hook we'll mock
    private ExchangeFunction exchange;

    private BookingRepository bookingRepository;
    private BookingRefGenerator refGenerator;
    private AvailabilityClientProperties props;

    private BookingService service;

    @BeforeEach
    void setup() {
        // Mock the ExchangeFunction
        exchange = mock(ExchangeFunction.class);

        // Build a WebClient that uses our mocked ExchangeFunction
        WebClient availabilityClient = WebClient.builder()
                .baseUrl("http://dummy") // base not used directly by tests
                .exchangeFunction(exchange)
                .build();

        // Properties (no retries for deterministic tests)
        props = new AvailabilityClientProperties();
        props.setBaseUrl("http://dummy");
        props.setPath("/api/bookings/checkAvailable");
        props.setRetries(0);
        props.setBackoffMillis(0);

        // Mongo mocks
        bookingRepository = mock(BookingRepository.class);
        refGenerator = mock(BookingRefGenerator.class);

        // Service under test
        service = new BookingService(availabilityClient, props, bookingRepository, refGenerator);
    }

    private static ClientResponse jsonResponse(HttpStatus status, String body) {
        return ClientResponse.create(status)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .build();
    }

    // -------- Availability tests --------

    @Test
    void checkAvailability_returns_true_when_availableSpace_gt_zero() {
        // Arrange: downstream returns {"availableSpace":6}
        when(exchange.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.OK, "{\"availableSpace\":6}")));

        AvailabilityRequest req = new AvailabilityRequest(20, "DRY", "DelhiX", "MumbaiY", 5);

        // Act & Assert
        StepVerifier.create(service.checkAvailability(req))
                .expectNextMatches(AvailabilityResponse::available) // should be true
                .verifyComplete();

        // Verify the external call was made
        verify(exchange).exchange(any(ClientRequest.class));
    }

    @Test
    void checkAvailability_returns_false_when_availableSpace_eq_zero() {
        when(exchange.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.just(jsonResponse(HttpStatus.OK, "{\"availableSpace\":0}")));

        AvailabilityRequest req = new AvailabilityRequest(40, "REEFER", "Chenna", "Vizagp", 3);

        StepVerifier.create(service.checkAvailability(req))
                .expectNextMatches(r -> !r.available()) // should be false
                .verifyComplete();
    }

    // -------- Create booking tests --------

    @Test
    void createBooking_happy_path_returns_bookingRef_and_saves_entity() {
        when(refGenerator.next()).thenReturn(Mono.just("957000123"));

        ArgumentCaptor<BookingEntity> entityCaptor = ArgumentCaptor.forClass(BookingEntity.class);
        when(bookingRepository.save(entityCaptor.capture()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0, BookingEntity.class)));

        BookingRequest req = new BookingRequest(
                20, "DRY", "Southampton", "Singapore", 5, "2020-10-12T13:53:09Z"
        );

        StepVerifier.create(service.createBooking(req))
                .expectNextMatches((BookingResponse r) -> "957000123".equals(r.bookingRef()))
                .verifyComplete();

        BookingEntity saved = entityCaptor.getValue();
        assertEquals("957000123", saved.getBookingRef());
        assertEquals(20, saved.getContainerSize());
        assertEquals("DRY", saved.getContainerType());
        assertEquals("Southampton", saved.getOrigin());
        assertEquals("Singapore", saved.getDestination());
        assertEquals(5, saved.getQuantity());
        assertEquals("2020-10-12T13:53:09Z", saved.getTimestamp());
    }

    @Test
    void createBooking_propagates_error_when_repository_fails() {
        when(refGenerator.next()).thenReturn(Mono.just("957000999"));
        when(bookingRepository.save(any(BookingEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("mongo unavailable")));

        BookingRequest req = new BookingRequest(
                40, "REEFER", "MumbaiA", "DelhiB", 2, "2021-01-01T00:00:00Z"
        );

        StepVerifier.create(service.createBooking(req))
                .expectErrorMatches(ex -> ex instanceof RuntimeException
                        && ex.getMessage().contains("mongo unavailable"))
                .verify();
    }
}
