package com.kaua.ecommerce.lib.domain.utils;

import com.kaua.ecommerce.lib.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PeriodTest extends UnitTest {

    @Test
    void givenAValidInstantPeriod_whenCreate_thenShouldNotThrowException() {
        var start = InstantUtils.now();
        var end = start.plusSeconds(1);

        final var aPeriod = new Period(start, end);

        Assertions.assertEquals(start, aPeriod.start());
        Assertions.assertEquals(end, aPeriod.end());
    }

    @Test
    void givenAnInvalidInstantPeriodOnStartBeforeEnd_whenCreate_thenShouldThrowException() {
        var start = InstantUtils.now();
        var end = start.minusSeconds(1);

        Assertions.assertThrows(DomainException.class, () -> new Period(start, end));
    }

    @Test
    void givenAnInvalidInstantPeriodOnStartNull_whenCreate_thenShouldThrowException() {
        var end = InstantUtils.now();

        Assertions.assertThrows(DomainException.class, () -> new Period(null, end));
    }

    @Test
    void givenAnInvalidInstantPeriodOnEndNull_whenCreate_thenShouldThrowException() {
        var start = InstantUtils.now();

        Assertions.assertThrows(DomainException.class, () -> new Period(start, null));
    }

    @Test
    void givenAValidStringPeriod_whenCreate_thenShouldNotThrowException() {
        var start = InstantUtils.now();
        var end = start.plusSeconds(1);

        final var aPeriod = new Period(start.toString(), end.toString());

        Assertions.assertEquals(start, aPeriod.start());
        Assertions.assertEquals(end, aPeriod.end());
    }
}
