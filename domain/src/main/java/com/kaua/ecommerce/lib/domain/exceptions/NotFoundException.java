package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.Identifier;
import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NotFoundException extends DomainException {

    public static final String ERROR_MESSAGE = "%s with id %s was not found";

    protected NotFoundException(final String aMessage, final List<Error> aErrors) {
        super(aMessage, aErrors);
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final String id
    ) {
        final var aError = ERROR_MESSAGE.formatted(anAggregate.getSimpleName(), id);

        return () -> new NotFoundException(aError, Collections.emptyList());
    }

    public static Supplier<NotFoundException> with(
            final String anAggregate,
            final String id
    ) {
        final var aError = ERROR_MESSAGE.formatted(anAggregate, id);

        return () -> new NotFoundException(aError, Collections.emptyList());
    }

    public static Supplier<NotFoundException> with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier<?> id
    ) {
        final var aError = ERROR_MESSAGE.formatted(
                anAggregate.getSimpleName(), id.value());

        return () -> new NotFoundException(aError, Collections.emptyList());
    }
}
