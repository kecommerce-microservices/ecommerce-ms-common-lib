package com.kaua.ecommerce.lib.domain.exceptions;

public class InternalErrorException extends NoStacktraceException {

    private final int statusCode;

    private InternalErrorException(final String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    private InternalErrorException(final String message, final Throwable cause, int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public static InternalErrorException with(final String message, int statusCode) {
        return new InternalErrorException(message, statusCode);
    }

    public static InternalErrorException with(final String message, final int statusCode, final Throwable cause) {
        return new InternalErrorException(message, cause, statusCode);
    }

    public static InternalErrorException with(final String message) {
        return new InternalErrorException(message, 0);
    }

    public static InternalErrorException with(final String message, final Throwable cause) {
        return new InternalErrorException(message, cause, 0);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
