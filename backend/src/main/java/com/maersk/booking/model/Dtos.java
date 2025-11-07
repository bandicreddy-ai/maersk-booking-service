package com.maersk.booking.model;

import com.maersk.booking.validation.OneOfInt;
import jakarta.validation.constraints.*;

public class Dtos {

    public record AvailabilityRequest(
            @OneOfInt({20, 40}) Integer containerSize,
            @Pattern(regexp = "DRY|REEFER") String containerType,
            @Size(min = 5, max = 20) String origin,
            @Size(min = 5, max = 20) String destination,
            @Min(1) @Max(100) Integer quantity
    ) {}

    public record AvailabilityResponse(boolean available) {}

    public record BookingRequest(
            @OneOfInt({20, 40}) Integer containerSize,
            @Pattern(regexp = "DRY|REEFER") String containerType,
            @Size(min = 5, max = 20) String origin,
            @Size(min = 5, max = 20) String destination,
            @Min(1) @Max(100) Integer quantity,
            @Pattern(
                regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$",
                message = "timestamp must be ISO-8601 UTC like 2020-10-12T13:53:09Z"
            ) String timestamp
    ) {}

    public record BookingResponse(String bookingRef) {}
}
