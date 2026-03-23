# Java Event Sourcing Sample

This repository now contains a minimal Java event sourcing sample built with Maven and Java 17.

## What the sample shows

- A `BankAccount` aggregate that changes state by applying immutable events.
- Event types for opening an account, depositing money, and withdrawing money.
- An in-memory event store that persists event streams by aggregate id.
- Rehydrating an aggregate by replaying stored events.
- JUnit tests that exercise replay, overdraft protection, and uncommitted changes.

## Project structure

- `src/main/java/com/example/eventsourcing/BankAccount.java` — aggregate root.
- `src/main/java/com/example/eventsourcing/BankAccountEvent.java` — event definitions.
- `src/main/java/com/example/eventsourcing/InMemoryEventStore.java` — simple event store.
- `src/main/java/com/example/eventsourcing/BankAccountRepository.java` — repository that saves and reloads aggregates from the store.
- `src/main/java/com/example/eventsourcing/SampleApplication.java` — runnable example.
- `src/test/java/com/example/eventsourcing/BankAccountTest.java` — sample tests.

## Run the sample

```bash
mvn test
mvn -q exec:java -Dexec.mainClass=com.example.eventsourcing.SampleApplication
```

If you do not have Maven installed locally, you can still inspect the source to see the event sourcing flow end-to-end.
