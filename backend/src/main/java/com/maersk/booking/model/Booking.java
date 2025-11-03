package com.maersk.booking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "bookings")
public class Booking {
    @Id
    private String bookingRef;

    @Field("container_size")
    private int containerSize;

    @Field("container_type")
    private ContainerType containerType;

    @Field("origin")
    private String origin;

    @Field("destination")
    private String destination;

    @Field("quantity")
    private int quantity;

    @Field("timestamp")
    private Instant timestamp;

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public int getContainerSize() { return containerSize; }
    public void setContainerSize(int containerSize) { this.containerSize = containerSize; }
    public ContainerType getContainerType() { return containerType; }
    public void setContainerType(ContainerType containerType) { this.containerType = containerType; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
