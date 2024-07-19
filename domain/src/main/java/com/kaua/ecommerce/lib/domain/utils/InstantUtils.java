package com.kaua.ecommerce.lib.domain.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public final class InstantUtils {

    public static final ChronoUnit DEFAULT_CHRONO_UNIT = ChronoUnit.MICROS;

    private InstantUtils() {}

    public static Instant now() {
        return Instant.now().truncatedTo(DEFAULT_CHRONO_UNIT);
    }

    public static Optional<Instant> fromString(final String date) {
        if (date == null || date.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(Instant.parse(date).truncatedTo(DEFAULT_CHRONO_UNIT));
    }
}
