package com.example.eventsourcing;

import java.math.BigDecimal;
import java.time.Clock;

public final class SampleApplication {
    private SampleApplication() {
    }

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        InMemoryEventStore store = new InMemoryEventStore();
        BankAccountRepository repository = new BankAccountRepository(store, clock);

        BankAccount account = BankAccount.open("acct-100", "Ada Lovelace", clock);
        account.deposit(new BigDecimal("150.00"));
        account.withdraw(new BigDecimal("40.00"));
        repository.save(account);

        BankAccount restored = repository.get("acct-100");
        System.out.printf(
                "Restored account %s owned by %s has balance %s and %d stored events.%n",
                restored.getId(),
                restored.getOwner(),
                restored.getBalance(),
                store.load(restored.getId()).size());
    }
}
