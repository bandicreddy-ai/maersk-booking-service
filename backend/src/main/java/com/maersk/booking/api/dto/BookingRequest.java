package com.maersk.booking.api.dto;

import com.maersk.booking.model.ContainerType;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Booking request")
public class BookingRequest {
    @NotNull
    private ContainerType containerType;

    @Min(20) @Max(40)
    private int containerSize;

    @Size(min = 5, max = 20)
    private String origin;

    @Size(min = 5, max = 20)
    private String destination;

    @Min(1) @Max(100)
    private int quantity;

    @NotNull
    private Instant timestamp;

    public ContainerType getContainerType() { return containerType; }
    public void setContainerType(ContainerType containerType) { this.containerType = containerType; }
    public int getContainerSize() { return containerSize; }
    public void setContainerSize(int containerSize) { this.containerSize = containerSize; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
