package com.kaua.ecommerce.lib.domain.validation;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;

public interface AssertionConcern {

    default <T> T assertArgumentNotNull(T val, String propertyName, String message) {
        if (val == null) {
            throw DomainException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentNotEmpty(String val, String propertyName, String message) {
        if (val == null || val.isBlank()) {
            throw DomainException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentMaxLength(String val, int max, String propertyName, String message) {
        if (val != null && val.length() > max) {
            throw DomainException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentMinLength(String val, int min, String propertyName, String message) {
        if (val != null && val.length() < min) {
            throw DomainException.with(new Error(propertyName, message));
        }
        return val;
    }

    default void assertConditionTrue(Boolean val, String propertyName, String message) {
        if (Boolean.FALSE.equals(val)) {
            throw DomainException.with(new Error(propertyName, message));
        }
    }

    default void assertArgumentGreaterThan(int val, int min, String propertyName, String message) {
        if (val <= min) {
            throw DomainException.with(new Error(propertyName, message));
        }
    }

    default void assertArgumentGreaterOrEquals(int val, int min, String propertyName, String message) {
        if (val < min) {
            throw DomainException.with(new Error(propertyName, message));
        }
    }
}