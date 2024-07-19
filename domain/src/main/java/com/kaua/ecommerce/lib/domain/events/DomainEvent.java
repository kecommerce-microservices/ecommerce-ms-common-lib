package com.kaua.ecommerce.lib.domain.events;

import com.kaua.ecommerce.lib.domain.validation.AssertionConcern;

import java.time.Instant;

public interface DomainEvent extends AssertionConcern {

    String aggregateId();
    String aggregateType(); // Order, Payment, etc
    long aggregateVersion();

    String eventId();
    String eventType(); // OrderCreated, PaymentConfirmed, etc
    String eventClassName(); // com.kaua.ecommerce.order.OrderCreated, com.kaua.ecommerce.payment.PaymentConfirmed, etc
    Instant occurredOn();

    String who();
    String traceId();
}
