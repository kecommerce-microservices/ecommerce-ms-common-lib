package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InternalErrorExceptionTest extends UnitTest {

    @Test
    void givenAMessage_whenCallInternalErrorExceptionWith_ThenReturnInternalErrorException() {
        // given
        final var message = "Internal Error";

        // when
        final var internalErrorException = InternalErrorException.with(message);

        // then
        Assertions.assertEquals(message, internalErrorException.getMessage());
    }

    @Test
    void givenAMessageAndACause_whenCallInternalErrorExceptionWith_ThenReturnInternalErrorException() {
        // given
        final var message = "Internal Error";
        final var cause = new RuntimeException("Cause");

        // when
        final var internalErrorException = InternalErrorException.with(message, cause);

        // then
        Assertions.assertEquals(message, internalErrorException.getMessage());
        Assertions.assertEquals(cause, internalErrorException.getCause());
    }

    @Test
    void givenAMessageAndCauseAndStatusCode_whenCallInternalErrorExceptionWith_ThenReturnInternalErrorException() {
        // given
        final var message = "Internal Error";
        final var cause = new RuntimeException("Cause");
        final var statusCode = 500;

        // when
        final var internalErrorException = InternalErrorException.with(message, statusCode, cause);

        // then
        Assertions.assertEquals(message, internalErrorException.getMessage());
        Assertions.assertEquals(cause, internalErrorException.getCause());
        Assertions.assertEquals(statusCode, internalErrorException.getStatusCode());
    }

    @Test
    void givenAMessageAndStatusCode_whenCallInternalErrorExceptionWith_ThenReturnInternalErrorException() {
        // given
        final var message = "Internal Error";
        final var statusCode = 500;

        // when
        final var internalErrorException = InternalErrorException.with(message, statusCode);

        // then
        Assertions.assertEquals(message, internalErrorException.getMessage());
        Assertions.assertEquals(statusCode, internalErrorException.getStatusCode());
    }
}
