package com.kaua.ecommerce.lib.infrastructure.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class ConflictException extends NoStacktraceException {

    private ConflictException(final String message) {
        super(message);
    }

    public static ConflictException with(final String message) {
        return new ConflictException(message);
    }
}
