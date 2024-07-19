package com.kaua.ecommerce.lib.domain.validation.handler;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.validation.Error;
import com.kaua.ecommerce.lib.domain.validation.ValidationHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationHandler implements ValidationHandler {

    private final List<Error> errors;

    private NotificationHandler(final List<Error> errors) {
        this.errors = errors;
    }

    public static NotificationHandler create() {
        return new NotificationHandler(new ArrayList<>());
    }

    public static NotificationHandler create(final Error anError) {
        return new NotificationHandler(new ArrayList<>()).append(anError);
    }

    @Override
    public NotificationHandler append(Error anError) {
        this.errors.add(anError);
        return this;
    }

    @Override
    public NotificationHandler append(ValidationHandler anHandler) {
        this.errors.addAll(anHandler.getErrors());
        return this;
    }

    @Override
    public <T> T validate(Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final DomainException ex) {
            this.errors.addAll(ex.getErrors());
        } catch (final Throwable t) {
            this.errors.add(new Error(t.getMessage()));
        }
        return null;
    }

    @Override
    public List<Error> getErrors() {
        return this.errors;
    }
}
