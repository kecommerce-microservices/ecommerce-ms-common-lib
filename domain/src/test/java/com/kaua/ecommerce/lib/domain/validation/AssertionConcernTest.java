package com.kaua.ecommerce.lib.domain.validation;

import com.kaua.ecommerce.lib.domain.UnitTest;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertionConcernTest extends UnitTest {

    @Test
    void givenANullName_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person(null, "email", 20, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenAnEmptyEmail_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "", 20, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenANullEmail_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", null, 20, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenANameWithLessThan3Characters_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("na", "email", 20, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenANameWithMoreThan50Characters_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("namessssssssssssssssssssssssssssssssssssssssssssssss", "email", 20, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenAnInvalidNegativeHeight_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 20, -1, true, "password", "phonenumber"));
    }

    @Test
    void givenAnInvalidAgeLessThan18_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 17, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenAnInvalidAgeEqualsTo18_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 18, 170, true, "password", "phonenumber"));
    }

    @Test
    void givenAnInvalidHeightLessThan150_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 20, 149, true, "password", "phonenumber"));
    }

    @Test
    void givenAFalseActive_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 20, 170, false, "password", "phonenumber"));
    }

    @Test
    void givenAnNullPassword_whenCreatePerson_shouldDoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> new Person("name", "email", 20, 170, true, null, "phonenumber"));
    }

    @Test
    void givenAnInvalidPasswordMoreThan20Characters_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 20, 170, true, "passwordpasswordpassword", "phonenumber"));
    }

    @Test
    void givenAnInvalidPhonenumberNull_whenCreatePerson_shouldDoesNotThrowException() {
        Assertions.assertDoesNotThrow(() -> new Person("name", "email", 20, 170, true, "password", null));
    }

    @Test
    void givenAnInvalidPhonenumberLessThan10Characters_whenCreatePerson_shouldThrowException() {
        Assertions.assertThrows(DomainException.class,
                () -> new Person("name", "email", 20, 170, true, "password", "123456789"));
    }

    @Test
    void givenAValidValues_whenCreatePerson_shouldNotThrowException() {
        Assertions.assertDoesNotThrow(() -> new Person("name", "email", 20, 170, true, "password", "1234567890"));
    }

    private record Person(
            String name,
            String email,
            int age,
            int height,
            boolean active,
            String password,
            String phonenumber
    ) implements AssertionConcern {

        public Person {
            this.assertArgumentNotNull(name, "name", "should not be null");
            this.assertArgumentNotEmpty(email, "email", "should not be empty");
            this.assertArgumentMaxLength(name, 50, "name", "should not exceed 50 characters");
            this.assertArgumentMinLength(name, 3, "name", "should not be less than 3 characters");
            this.assertConditionTrue(active, "active", "should be true");
            this.assertArgumentGreaterThan(age, 18, "age", "should be greater than 18");
            this.assertArgumentGreaterOrEquals(height, 150, "height", "should be greater or equals to 150");
            this.assertArgumentMaxLength(password, 20, "password", "should not exceed 20 characters");
            this.assertArgumentMinLength(phonenumber, 10, "phonenumber", "should not be less than 10 characters");
        }
    }
}
