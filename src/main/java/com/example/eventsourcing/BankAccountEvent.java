package com.example.eventsourcing;

import java.math.BigDecimal;
import java.time.Instant;

public sealed interface BankAccountEvent extends DomainEvent
        permits BankAccountEvent.AccountOpened, BankAccountEvent.MoneyDeposited, BankAccountEvent.MoneyWithdrawn {

    record AccountOpened(String aggregateId, String owner, Instant occurredAt) implements BankAccountEvent {
    }

    record MoneyDeposited(String aggregateId, BigDecimal amount, Instant occurredAt) implements BankAccountEvent {
    }

    record MoneyWithdrawn(String aggregateId, BigDecimal amount, Instant occurredAt) implements BankAccountEvent {
    }
}
