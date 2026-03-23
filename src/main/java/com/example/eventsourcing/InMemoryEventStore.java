package com.example.eventsourcing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryEventStore {
    private final Map<String, List<BankAccountEvent>> streams = new HashMap<>();

    public void append(String aggregateId, List<BankAccountEvent> newEvents) {
        streams.computeIfAbsent(aggregateId, ignored -> new ArrayList<>()).addAll(newEvents);
    }

    public List<BankAccountEvent> load(String aggregateId) {
        return List.copyOf(streams.getOrDefault(aggregateId, List.of()));
    }
}
