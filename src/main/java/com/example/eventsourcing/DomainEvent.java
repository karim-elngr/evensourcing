package com.example.eventsourcing;

import java.time.Instant;

public interface DomainEvent {
    String aggregateId();
    Instant occurredAt();
}
