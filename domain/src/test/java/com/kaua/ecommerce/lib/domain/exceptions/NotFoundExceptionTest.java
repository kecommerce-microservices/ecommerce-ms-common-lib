package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.AggregateRoot;
import com.kaua.ecommerce.lib.domain.Identifier;
import com.kaua.ecommerce.lib.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NotFoundExceptionTest extends UnitTest {

    @Test
    void givenAValidAggregate_whenCallNotFoundExceptionWith_ThenReturnNotFoundException() {
        // given
        final var aggregate = SampleAggregate.class;
        final var aId = "123";
        final var expectedErrorMessage = "SampleAggregate with id 123 was not found";

        // when
        final var notFoundException = NotFoundException.with(aggregate, aId);
        // then
        Assertions.assertEquals(expectedErrorMessage, notFoundException.get().getMessage());
    }

    @Test
    void givenAValidAggregateStringName_whenCallNotFoundExceptionWith_ThenReturnNotFoundException() {
        // given
        final var aggregate = "SampleAggregate";
        final var aId = "123";
        final var expectedErrorMessage = "SampleAggregate with id 123 was not found";

        // when
        final var notFoundException = NotFoundException.with(aggregate, aId);
        // then
        Assertions.assertEquals(expectedErrorMessage, notFoundException.get().getMessage());
    }

    @Test
    void givenAValidAggregateUUID_whenCallNotFoundExceptionWith_ThenReturnNotFoundException() {
        // given
        final var aggregate = SampleAggregate.class;
        final var aId = new SampleUUIDIdentifier(UUID.randomUUID());
        final var expectedErrorMessage = "SampleAggregate with id %s was not found"
                .formatted(aId.value());

        // when
        final var notFoundException = NotFoundException.with(aggregate, aId);

        // then
        Assertions.assertEquals(expectedErrorMessage, notFoundException.get().getMessage());
    }

    private static class SampleAggregate extends AggregateRoot<SampleIdentifier> {
        public SampleAggregate(SampleIdentifier id) {
            super(id);
        }
    }

    private record SampleIdentifier(String value) implements Identifier<String> {
    }

    private record SampleUUIDIdentifier(UUID value) implements Identifier<UUID> {
    }
}
