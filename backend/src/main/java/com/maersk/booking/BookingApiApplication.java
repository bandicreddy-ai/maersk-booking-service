package com.maersk.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingApiApplication {
    private static final Logger log = LoggerFactory.getLogger(BookingApiApplication.class);
    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        SpringApplication.run(BookingApiApplication.class, args);
        log.info("Booking API started in {} ms", (System.currentTimeMillis() - t0));
    }
}
