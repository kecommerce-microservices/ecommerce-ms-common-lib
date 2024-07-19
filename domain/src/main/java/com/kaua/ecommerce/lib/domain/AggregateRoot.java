package com.kaua.ecommerce.lib.domain;

import com.kaua.ecommerce.lib.domain.events.DomainEvent;

import java.util.List;

public abstract class AggregateRoot<ID extends Identifier> extends Entity<ID> {

    protected AggregateRoot(final ID id) {
        super(id);
    }

    protected AggregateRoot(final ID id, final long version) {
        super(id, version);
    }

    protected AggregateRoot(final ID id, final long version, final List<DomainEvent> domainEvents) {
        super(id, version, domainEvents);
    }
}
