package com.kaua.ecommerce.lib.domain;

import com.kaua.ecommerce.lib.domain.events.DomainEvent;
import com.kaua.ecommerce.lib.domain.events.DomainEventPublisher;
import com.kaua.ecommerce.lib.domain.validation.AssertionConcern;
import com.kaua.ecommerce.lib.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Entity<ID extends Identifier> implements AssertionConcern {

    protected final ID id;
    private long version;
    private final List<DomainEvent> domainEvents;

    protected Entity(final ID id) {
        this(id, 0, null);
    }

    protected Entity(final ID id, final long version) {
        this(id, version, null);
    }

    protected Entity(final ID id, final long version, final List<DomainEvent> domainEvents) {
        this.id = this.assertArgumentNotNull(id, "id", "should not be null");
        this.version = version;
        this.domainEvents = new ArrayList<>(domainEvents == null ? Collections.emptyList() : domainEvents);
    }

    public void validate(ValidationHandler aHandler) {}

    public ID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long incrementVersion() {
        return ++this.version;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void registerEvent(final DomainEvent aEvent) {
        if (aEvent == null) {
            return;
        }

        this.domainEvents.add(aEvent);
    }

    public void publishDomainEvents(final DomainEventPublisher publisher) {
        if (publisher == null) {
            return;
        }

        getDomainEvents().forEach(publisher::publish);

        this.domainEvents.clear();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return getId().equals(entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
