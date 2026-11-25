package com.example.eventsourcing;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BankAccount {
    private final String id;
    private final Clock clock;
    private String owner;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean opened;
    private final List<BankAccountEvent> uncommittedChanges = new ArrayList<>();

    private BankAccount(String id, Clock clock) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public static BankAccount open(String id, String owner, Clock clock) {
        if (owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }

        BankAccount account = new BankAccount(id, clock);
        account.record(new BankAccountEvent.AccountOpened(id, owner, clock.instant()));
        return account;
    }

    public static BankAccount rehydrate(String id, Clock clock, List<BankAccountEvent> history) {
        BankAccount account = new BankAccount(id, clock);
        history.forEach(account::apply);
        return account;
    }

    public void deposit(BigDecimal amount) {
        ensureOpen();
        validatePositive(amount, "deposit");
        record(new BankAccountEvent.MoneyDeposited(id, amount, clock.instant()));
    }

    public void withdraw(BigDecimal amount) {
        ensureOpen();
        validatePositive(amount, "withdrawal");
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("insufficient funds");
        }
        record(new BankAccountEvent.MoneyWithdrawn(id, amount, clock.instant()));
    }

    public List<BankAccountEvent> getUncommittedChanges() {
        return List.copyOf(uncommittedChanges);
    }

    public void markChangesAsCommitted() {
        uncommittedChanges.clear();
    }

    public String getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    private void record(BankAccountEvent event) {
        apply(event);
        uncommittedChanges.add(event);
    }

    private void apply(BankAccountEvent event) {
        switch (event) {
            case BankAccountEvent.AccountOpened openedEvent -> {
                opened = true;
                owner = openedEvent.owner();
            }
            case BankAccountEvent.MoneyDeposited depositedEvent ->
                    balance = balance.add(depositedEvent.amount());
            case BankAccountEvent.MoneyWithdrawn withdrawnEvent ->
                    balance = balance.subtract(withdrawnEvent.amount());
        }
    }

    private void ensureOpen() {
        if (!opened) {
            throw new IllegalStateException("account is not open");
        }
    }

    private static void validatePositive(BigDecimal amount, String action) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(action + " amount must be positive");
        }
    }
}
