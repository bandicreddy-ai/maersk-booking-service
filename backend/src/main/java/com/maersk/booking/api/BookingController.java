package com.maersk.booking.api;

import com.maersk.booking.api.dto.AvailabilityRequest;
import com.maersk.booking.api.dto.AvailabilityResponse;
import com.maersk.booking.api.dto.BookingRequest;
import com.maersk.booking.api.dto.BookingResponse;
import com.maersk.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings")
@SecurityRequirement(name = "ApiKeyAuth")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping(value = "/availability", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Check availability of containers")
    public Mono<ResponseEntity<AvailabilityResponse>> availability(@Valid @RequestBody AvailabilityRequest req) {
        return bookingService.checkAvailability(req)
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new booking")
    public Mono<ResponseEntity<BookingResponse>> create(@Valid @RequestBody BookingRequest req) {
        return bookingService.createBooking(req)
                .map(ResponseEntity::ok);
    }
}
