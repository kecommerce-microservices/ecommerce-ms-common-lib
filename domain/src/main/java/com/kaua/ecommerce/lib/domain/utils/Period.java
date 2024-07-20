package com.kaua.ecommerce.lib.domain.utils;

import com.kaua.ecommerce.lib.domain.validation.AssertionConcern;

import java.time.Instant;

public record Period(
        Instant start,
        Instant end
) implements AssertionConcern {

    public Period {
        this.assertArgumentNotNull(start, "start", "should not be null");
        this.assertArgumentNotNull(end, "end", "should not be null");
        this.assertConditionTrue(start.compareTo(end) < 0, "start", "should be before end");
    }

    public Period(final String start, final String end) {
        this(
                InstantUtils.fromString(start).orElse(null),
                InstantUtils.fromString(end).orElse(null)
        );
    }
}
