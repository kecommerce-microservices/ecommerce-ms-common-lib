package com.kaua.ecommerce.lib.infrastructure.exceptions;

import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;

public class UnauthorizedException extends NoStacktraceException {

    private UnauthorizedException(final String message) {
        super(message);
    }

    public static UnauthorizedException with(final String message) {
        return new UnauthorizedException(message);
    }
}
