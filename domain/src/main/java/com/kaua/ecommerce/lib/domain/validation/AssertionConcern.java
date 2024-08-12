package com.kaua.ecommerce.lib.domain.validation;

import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;

import java.util.List;
import java.util.Set;

public interface AssertionConcern {

    default <T> T assertArgumentNotNull(T val, String propertyName, String message) {
        if (val == null) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentNotEmpty(String val, String propertyName, String message) {
        if (val == null || val.isBlank()) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentLength(String val, int length, String propertyName, String aMessage) {
        if (val == null || val.length() != length) {
            throw ValidationException.with(new Error(propertyName, aMessage));
        }
        return val;
    }

    default String assertArgumentMaxLength(String val, int max, String propertyName, String message) {
        if (val != null && val.length() > max) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }

    default String assertArgumentMinLength(String val, int min, String propertyName, String message) {
        if (val != null && val.length() < min) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }

    default void assertConditionTrue(Boolean val, String propertyName, String message) {
        if (Boolean.FALSE.equals(val)) {
            throw ValidationException.with(new Error(propertyName, message));
        }
    }

    default void assertArgumentGreaterThan(int val, int min, String propertyName, String message) {
        if (val <= min) {
            throw ValidationException.with(new Error(propertyName, message));
        }
    }

    default void assertArgumentGreaterOrEquals(int val, int min, String propertyName, String message) {
        if (val < min) {
            throw ValidationException.with(new Error(propertyName, message));
        }
    }

    default void assertArgumentPattern(String val, String pattern, String propertyName, String message) {
        if (val != null && !val.matches(pattern)) {
            throw ValidationException.with(new Error(propertyName, message));
        }
    }

    default <T> Set<T> assertArgumentNotEmpty(Set<T> val, String propertyName, String message) {
        if (val == null || val.isEmpty()) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }

    default <T> List<T> assertArgumentNotEmpty(List<T> val, String propertyName, String message) {
        if (val == null || val.isEmpty()) {
            throw ValidationException.with(new Error(propertyName, message));
        }
        return val;
    }
}
