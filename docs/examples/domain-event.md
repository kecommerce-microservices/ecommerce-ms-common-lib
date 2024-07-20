# Exemplo de uso de Domain Event

```java
public record AccountCreatedEvent(
        String aggregateId,
        String aggregateType,
        long aggregateVersion,
        String eventId,
        String eventType,
        String eventClassName,
        Instant occurredOn,
        String who,
        String traceId
) implements DomainEvent {
    
    public AccountCreatedEvent(
            String aggregateId,
            long aggregateVersion,
            String who,
            String traceId
    ) {
        this(
                aggregateId,
                "Account",
                aggregateVersion,
                UUID.randomUUID().toString(),
                "AccountCreated",
                AccountCreatedEvent.class.getName(),
                InstantUtils.now(),
                who,
                traceId
        );
    }
}
```