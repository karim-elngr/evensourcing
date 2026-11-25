package com.example.eventsourcing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.Test;

class BankAccountTest {
    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void rehydratesBalanceFromStoredEvents() {
        InMemoryEventStore eventStore = new InMemoryEventStore();
        BankAccountRepository repository = new BankAccountRepository(eventStore, fixedClock);

        BankAccount account = BankAccount.open("acct-1", "Ada", fixedClock);
        account.deposit(new BigDecimal("100.00"));
        account.withdraw(new BigDecimal("35.50"));
        repository.save(account);

        BankAccount restored = repository.get("acct-1");

        assertEquals("Ada", restored.getOwner());
        assertEquals(new BigDecimal("64.50"), restored.getBalance());
        assertEquals(3, eventStore.load("acct-1").size());
    }

    @Test
    void rejectsOverdrafts() {
        BankAccount account = BankAccount.open("acct-2", "Grace", fixedClock);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> account.withdraw(new BigDecimal("1.00")));

        assertEquals("insufficient funds", exception.getMessage());
    }

    @Test
    void tracksOnlyNewEventsAsUncommittedChanges() {
        List<BankAccountEvent> history = List.of(
                new BankAccountEvent.AccountOpened("acct-3", "Linus", fixedClock.instant()),
                new BankAccountEvent.MoneyDeposited("acct-3", new BigDecimal("20.00"), fixedClock.instant()));

        BankAccount restored = BankAccount.rehydrate("acct-3", fixedClock, history);
        restored.deposit(new BigDecimal("5.00"));

        assertEquals(1, restored.getUncommittedChanges().size());
        assertEquals(new BigDecimal("25.00"), restored.getBalance());
    }
}
