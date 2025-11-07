package com.maersk.booking.mongo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BookingRepository extends ReactiveCrudRepository<BookingEntity, String> {}
