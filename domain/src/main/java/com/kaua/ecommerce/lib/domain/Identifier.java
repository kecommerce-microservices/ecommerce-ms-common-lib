package com.kaua.ecommerce.lib.domain;

public interface Identifier<T> extends ValueObject {

    T value();
}
