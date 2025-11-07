package com.maersk.booking.web;

import com.maersk.booking.model.Dtos.*;
import com.maersk.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Bookings")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Check container availability")
    @PostMapping(path = "/availability", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AvailabilityResponse>> availability(@Valid @RequestBody AvailabilityRequest request) {
        log.debug("POST /api/bookings/availability");
        return bookingService.checkAvailability(request).map(ResponseEntity::ok);
    }

    @Operation(summary = "Create a booking")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<BookingResponse>> create(@Valid @RequestBody BookingRequest request) {
        log.debug("POST /api/bookings");
        return bookingService.createBooking(request).map(ResponseEntity::ok);
    }
}
