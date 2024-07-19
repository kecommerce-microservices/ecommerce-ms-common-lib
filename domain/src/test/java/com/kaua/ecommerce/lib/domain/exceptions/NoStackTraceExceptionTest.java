package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.UnitTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoStackTraceExceptionTest extends UnitTest {

    @Test
    void testCallNoStacktraceExceptionWithoutCause() {
        final var exception = new NoStacktraceException("message");

        Assertions.assertEquals("message", exception.getMessage());
        Assertions.assertNull(exception.getCause());
    }

    @Test
    void testCallNoStacktraceExceptionWithCause() {
        final var cause = new RuntimeException();
        final var exception = new NoStacktraceException("message", cause);

        Assertions.assertEquals("message", exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
    }
}
