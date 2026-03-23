package com.example.eventsourcing;

import java.time.Clock;

public final class BankAccountRepository {
    private final InMemoryEventStore eventStore;
    private final Clock clock;

    public BankAccountRepository(InMemoryEventStore eventStore, Clock clock) {
        this.eventStore = eventStore;
        this.clock = clock;
    }

    public BankAccount get(String id) {
        return BankAccount.rehydrate(id, clock, eventStore.load(id));
    }

    public void save(BankAccount account) {
        eventStore.append(account.getId(), account.getUncommittedChanges());
        account.markChangesAsCommitted();
    }
}
