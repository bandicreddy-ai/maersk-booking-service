package com.maersk.booking.service;

import com.maersk.booking.api.dto.AvailabilityRequest;
import com.maersk.booking.api.dto.AvailabilityResponse;
import com.maersk.booking.api.dto.BookingRequest;
import com.maersk.booking.api.dto.BookingResponse;
import com.maersk.booking.model.Booking;
import com.maersk.booking.model.Counter;
import com.maersk.booking.repo.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ReactiveMongoOperations mongoOps;
    private final WebClient webClient;
    private final String externalPath;
    private final long sequenceStart;

    public BookingService(
            BookingRepository bookingRepository,
            ReactiveMongoOperations mongoOps,
            WebClient.Builder webClientBuilder,
            @Value("${app.external.base-url}") String baseUrl,
            @Value("${app.external.availability-path}") String externalPath,
            @Value("${app.booking.sequence-start}") long sequenceStart
    ) {
        this.bookingRepository = bookingRepository;
        this.mongoOps = mongoOps;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.externalPath = externalPath;
        this.sequenceStart = sequenceStart;
    }

    public Mono<AvailabilityResponse> checkAvailability(AvailabilityRequest req) {
        return webClient.post()
                .uri(externalPath)
                .bodyValue(Map.of(
                        "containerType", req.getContainerType().name(),
                        "containerSize", req.getContainerSize(),
                        "origin", req.getOrigin(),
                        "destination", req.getDestination(),
                        "quantity", req.getQuantity()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .map(map -> {
                    Object v = map.get("availableSpace");
                    int available = (v instanceof Number) ? ((Number) v).intValue() : 0;
                    return new AvailabilityResponse(available > 0);
                })
                .onErrorReturn(new AvailabilityResponse(false));
    }

    public Mono<BookingResponse> createBooking(BookingRequest req) {
        return nextSequence("bookingRef").map(seq -> {
            String bookingRef = String.valueOf(seq);
            Booking b = new Booking();
            b.setBookingRef(bookingRef);
            b.setContainerSize(req.getContainerSize());
            b.setContainerType(req.getContainerType());
            b.setOrigin(req.getOrigin());
            b.setDestination(req.getDestination());
            b.setQuantity(req.getQuantity());
            b.setTimestamp(req.getTimestamp());
            return b;
        }).flatMap(bookingRepository::save)
          .map(saved -> new BookingResponse(saved.getBookingRef()))
          .onErrorMap(ex -> new RuntimeException("PERSISTENCE_ERROR", ex));
    }

    private Mono<Long> nextSequence(String name) {
        Query query = Query.query(Criteria.where("_id").is(name));
        Update update = new Update().inc("seq", 1);
        return mongoOps.findAndModify(query, update, Counter.class)
                .switchIfEmpty(mongoOps.save(new Counter(name, sequenceStart)).then(mongoOps.findAndModify(query, update, Counter.class)))
                .map(Counter::getSeq)
                .map(seq -> (seq <= sequenceStart) ? sequenceStart + 1 : seq);
    }
}
