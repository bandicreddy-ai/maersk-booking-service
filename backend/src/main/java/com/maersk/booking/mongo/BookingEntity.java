package com.maersk.booking.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "bookings")
public class BookingEntity {
    @Id
    private String bookingRef;

    @Field("container_size")
    private Integer containerSize;

    @Field("container_type")
    private String containerType;

    @Field("origin")
    private String origin;

    @Field("destination")
    private String destination;

    @Field("quantity")
    private Integer quantity;

    @Field("timestamp")
    private String timestamp;

    public BookingEntity() {}

    public BookingEntity(String bookingRef, Integer containerSize, String containerType, String origin,
                         String destination, Integer quantity, String timestamp) {
        this.bookingRef = bookingRef;
        this.containerSize = containerSize;
        this.containerType = containerType;
        this.origin = origin;
        this.destination = destination;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getBookingRef() { return bookingRef; }
    public void setBookingRef(String bookingRef) { this.bookingRef = bookingRef; }
    public Integer getContainerSize() { return containerSize; }
    public void setContainerSize(Integer containerSize) { this.containerSize = containerSize; }
    public String getContainerType() { return containerType; }
    public void setContainerType(String containerType) { this.containerType = containerType; }
    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
