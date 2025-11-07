package com.maersk.booking;

import com.maersk.booking.model.Dtos.*;
import com.maersk.booking.service.BookingService;
import com.maersk.booking.web.BookingController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

public class BookingControllerTest {

    private WebTestClient client;
    private BookingService service;

    @BeforeEach
    void setup() {
        service = Mockito.mock(BookingService.class);
        BookingController controller = new BookingController(service);
        client = WebTestClient.bindToController(controller).build();
    }

    @Test
    void availability_true() {
        Mockito.when(service.checkAvailability(ArgumentMatchers.any()))
                .thenReturn(Mono.just(new AvailabilityResponse(true)));

        client.post().uri("/api/bookings/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "containerSize", 20,
                        "containerType", "DRY",
                        "origin", "DelhiX",
                        "destination", "MumbaiY",
                        "quantity", 5
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.available").isEqualTo(true);
    }

    @Test
    void create_booking() {
        Mockito.when(service.createBooking(ArgumentMatchers.any()))
                .thenReturn(Mono.just(new BookingResponse("957000001")));

        client.post().uri("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "containerSize", 20,
                        "containerType", "DRY",
                        "origin", "DelhiX",
                        "destination", "MumbaiY",
                        "quantity", 1,
                        "timestamp", "2020-10-12T13:53:09Z"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingRef").isEqualTo("957000001");
    }
}
