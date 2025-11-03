package com.maersk.booking.api;

import com.maersk.booking.api.dto.*;
import com.maersk.booking.model.ContainerType;
import com.maersk.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class BookingControllerTest {

    @Test
    void endpoints_work(){
        BookingService svc = Mockito.mock(BookingService.class);
        Mockito.when(svc.checkAvailability(Mockito.any())).thenReturn(Mono.just(new AvailabilityResponse(true)));
        Mockito.when(svc.createBooking(Mockito.any())).thenReturn(Mono.just(new BookingResponse("957000001")));

        BookingController controller = new BookingController(svc);
        WebTestClient client = WebTestClient.bindToController(controller).build();

        AvailabilityRequest ar = new AvailabilityRequest();
        ar.setContainerType(ContainerType.DRY);
        ar.setContainerSize(20);
        ar.setOrigin("OriginA");
        ar.setDestination("DestinA");
        ar.setQuantity(5);

        client.post().uri("/api/bookings/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ar)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.available").isEqualTo(true);

        BookingRequest br = new BookingRequest();
        br.setContainerType(ContainerType.DRY);
        br.setContainerSize(20);
        br.setOrigin("OriginA");
        br.setDestination("DestinA");
        br.setQuantity(5);
        br.setTimestamp(Instant.now());

        client.post().uri("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(br)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.bookingRef").isEqualTo("957000001");
    }
}
