package com.kaua.ecommerce.lib.infrastructure.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class ForbiddenException extends NoStacktraceException {

    private ForbiddenException(final String message) {
        super(message);
    }

    public static ForbiddenException with(final String message) {
        return new ForbiddenException(message);
    }
}
