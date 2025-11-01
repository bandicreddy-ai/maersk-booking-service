package com.maersk.booking.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
public class BookingController {
    //Testing To do
    @GetMapping
    public String testController(  ) {
        return "Testing Controller class behaviour";
    }

}
