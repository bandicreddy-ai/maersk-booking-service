package com.maersk.booking.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maersk.booking.api.dto.AvailabilityRequest;
import com.maersk.booking.model.ContainerType;
import com.maersk.booking.repo.BookingRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import com.maersk.booking.api.dto.BookingRequest;
import com.maersk.booking.model.Booking;

public class BookingServiceTest {

    static WireMockServer wm;
    BookingService service;
    BookingRepository repo;
    ReactiveMongoOperations ops;

    @BeforeAll
    static void startWireMock(){
        wm = new WireMockServer(0);
        wm.start();
    }

    @AfterAll
    static void stopWireMock(){
        wm.stop();
    }

    @BeforeEach
    void setup(){
        repo = Mockito.mock(BookingRepository.class);
        ops = Mockito.mock(ReactiveMongoOperations.class);

        String baseUrl = "http://localhost:" + wm.port();
        service = new BookingService(repo, ops, WebClient.builder(), baseUrl, "/api/bookings/checkAvailable", 957000000L);
    }

    @Test
    void availability_whenAvailableSpaceZero_returnsFalse(){
        wm.stubFor(WireMock.post("/api/bookings/checkAvailable")
                .willReturn(WireMock.aResponse().withStatus(200).withBody("{\"availableSpace\":0}")));
        AvailabilityRequest req = new AvailabilityRequest();
        req.setContainerType(ContainerType.DRY);
        req.setContainerSize(20);
        req.setOrigin("OriginA");
        req.setDestination("DestinA");
        req.setQuantity(5);

        StepVerifier.create(service.checkAvailability(req))
                .expectNextMatches(res -> !res.available())
                .verifyComplete();
    }

    @Test
    void availability_whenAvailableSpacePositive_returnsTrue(){
        wm.stubFor(WireMock.post("/api/bookings/checkAvailable")
                .willReturn(WireMock.aResponse().withStatus(200).withBody("{\"availableSpace\":3}")));
        AvailabilityRequest req = new AvailabilityRequest();
        req.setContainerType(ContainerType.DRY);
        req.setContainerSize(20);
        req.setOrigin("OriginA");
        req.setDestination("DestinA");
        req.setQuantity(5);

        StepVerifier.create(service.checkAvailability(req))
                .expectNextMatches(res -> res.available())
                .verifyComplete();
    }

    @Test
    void createBooking_savesAndReturnsRef(){
        BookingRequest br = new BookingRequest();
        br.setContainerType(ContainerType.DRY);
        br.setContainerSize(20);
        br.setOrigin("OriginA");
        br.setDestination("DestinA");
        br.setQuantity(5);
        br.setTimestamp(Instant.now());

        Mockito.when(ops.findAndModify(Mockito.any(), Mockito.any(), Mockito.eq(com.maersk.booking.model.Counter.class)))
                .thenReturn(Mono.empty());
        Mockito.when(ops.save(Mockito.any(com.maersk.booking.model.Counter.class))).thenReturn(Mono.just(new com.maersk.booking.model.Counter("bookingRef", 957000000L)));
        Mockito.when(ops.findAndModify(Mockito.any(), Mockito.any(), Mockito.eq(com.maersk.booking.model.Counter.class)))
                .thenReturn(Mono.just(new com.maersk.booking.model.Counter("bookingRef", 957000001L)));

        Mockito.when(repo.save(Mockito.any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            return Mono.just(b);
        });

        StepVerifier.create(service.createBooking(br))
                .expectNextMatches(r -> r.bookingRef().equals("957000001"))
                .verifyComplete();
    }
}
