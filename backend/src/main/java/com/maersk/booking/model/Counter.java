package com.maersk.booking.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "counters")
public class Counter {
    @Id
    private String id;

    @Field("seq")
    private long seq;

    public Counter() {}
    public Counter(String id, long seq) { this.id = id; this.seq = seq; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public long getSeq() { return seq; }
    public void setSeq(long seq) { this.seq = seq; }
}
