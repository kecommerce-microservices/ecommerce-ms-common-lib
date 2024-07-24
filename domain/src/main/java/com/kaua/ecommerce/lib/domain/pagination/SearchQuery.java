package com.kaua.ecommerce.lib.domain.pagination;

import com.kaua.ecommerce.lib.domain.utils.Period;

import java.util.Optional;

public record SearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction,
        Period period
) {

    public SearchQuery(
            int page,
            int perPage,
            String terms,
            String sort,
            String direction
    ) {
        this(page, perPage, terms, sort, direction, null);
    }

    public Optional<Period> getPeriod() {
        return Optional.ofNullable(period);
    }
}
