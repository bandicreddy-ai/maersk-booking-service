package com.maersk.booking.repo;

import com.maersk.booking.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BookingRepository extends ReactiveMongoRepository<Booking, String> { }
