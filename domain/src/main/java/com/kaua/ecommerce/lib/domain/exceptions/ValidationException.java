package com.kaua.ecommerce.lib.domain.exceptions;

import com.kaua.ecommerce.lib.domain.validation.Error;

import java.util.List;

public class ValidationException extends DomainException {

    private ValidationException(final List<Error> aErrors) {
        super("ValidationException", aErrors);
    }

    public static ValidationException with(final List<Error> aErrors) {
        return new ValidationException(aErrors);
    }

    public static ValidationException with(final Error aError) {
        return new ValidationException(List.of(aError));
    }
}
