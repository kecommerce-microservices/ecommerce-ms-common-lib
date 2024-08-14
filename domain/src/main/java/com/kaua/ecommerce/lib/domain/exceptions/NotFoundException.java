package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.Identifier;
import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NotFoundException extends NoStacktraceException {

    public static final String ERROR_MESSAGE = "%s with %s %s was not found";

    protected NotFoundException(final String aMessage) {
        super(aMessage);
    }

    public static NotFoundException with(final String aMessage) {
        return new NotFoundException(aMessage);
    }

    public static Supplier<NotFoundException> with(
            final String anAggregate,
            final String aIdentifierField,
            final String aIdentifierValue
    ) {
        final var aError = ERROR_MESSAGE.formatted(
                anAggregate,
                aIdentifierField,
                aIdentifierValue
        );

        return () -> new NotFoundException(aError);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String aIdentifierField,
            final String aIdentifierValue
    ) {
        return with(anAggregate.getSimpleName(), aIdentifierField, aIdentifierValue);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String aIdentifierField,
            final Identifier<?> id
    ) {
        return with(anAggregate.getSimpleName(), aIdentifierField, id.value().toString());
    }

    public static Supplier<NotFoundException> with(
            final String anAggregate,
            final String aId
    ) {
        return with(anAggregate, "id", aId);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String aId
    ) {
        return with(anAggregate.getSimpleName(), "id", aId);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier<?> id
    ) {
        return with(anAggregate.getSimpleName(), "id", id.value().toString());
    }
}
