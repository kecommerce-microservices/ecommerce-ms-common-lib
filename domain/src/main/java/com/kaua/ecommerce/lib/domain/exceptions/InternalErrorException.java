package com.kaua.ecommerce.lib.domain.exceptions;

public class InternalErrorException extends NoStacktraceException{

    private InternalErrorException(final String message) {
        super(message);
    }

    private InternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InternalErrorException with(final String message) {
        return new InternalErrorException(message);
    }

    public static InternalErrorException with(final String message, final Throwable cause) {
        return new InternalErrorException(message, cause);
    }
}
