package com.maersk.booking.api.dto;

import com.maersk.booking.model.ContainerType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Availability check request")
public class AvailabilityRequest {
    @NotNull @Schema(enumAsRef = true, description = "DRY or REEFER")
    private ContainerType containerType;

    @Min(20) @Max(40)
    private int containerSize;

    @Size(min = 5, max = 20)
    private String origin;

    @Size(min = 5, max = 20)
    private String destination;

    @Min(1) @Max(100)
    private int quantity;

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
}
