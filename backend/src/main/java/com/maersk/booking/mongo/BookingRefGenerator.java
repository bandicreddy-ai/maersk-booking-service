package com.maersk.booking.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BookingRefGenerator {
    private static final Logger log = LoggerFactory.getLogger(BookingRefGenerator.class);

    private static final String SEQ_NAME = "bookingRef";
    private static final long BASE = 957_000_000L; // +1 => 957000001

    private final ReactiveMongoTemplate template;

    public BookingRefGenerator(ReactiveMongoTemplate template) {
        this.template = template;
    }

    public Mono<String> next() {
        log.debug("Generating next bookingRef...");
        Query q = new Query(Criteria.where("_id").is(SEQ_NAME));
        Update u = new Update().inc("value", 1);
        return template.findAndModify(q, u,
                org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true).upsert(true),
                Sequence.class, "counters")
                .defaultIfEmpty(new Sequence(SEQ_NAME, 1))
                .map(seq -> {
                    String ref = Long.toString(BASE + seq.value());
                    log.info("Generated bookingRef={}", ref);
                    return ref;
                });
    }

    static class Sequence {
        private String id;
        private long value;
        public Sequence() {}
        public Sequence(String id, long value) { this.id = id; this.value = value; }
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public long value() { return value; }
        public void setValue(long value) { this.value = value; }
    }
}
