package com.kaua.ecommerce.lib.domain.events;

@FunctionalInterface
public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(final T aDomainEvent);
}
