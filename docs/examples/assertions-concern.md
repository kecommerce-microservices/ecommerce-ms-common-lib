# Exemplos utilizando o AssertionConcern
Aqui está um exemplo simples, ainda temos o greater, o condition true e o not null.
```java
public record Person(
    String name,
    String email,
    String phone,
    String cpf,
    int age
) implements AssertionConcern {
    
    public Person {
        this.assertArgumentNotEmpty(name, "name", "should not be empty");
        this.assertArgumentNotEmpty(email, "email", "should not be empty");
        this.assertArgumentNotEmpty(phone, "phone", "should not be empty");
        this.assertArgumentNotEmpty(cpf, "cpf", "should not be empty");
        this.assertArgumentMinLength(cpf, 11, "cpf", "should not be less than 11 characters");
        this.assertArgumentMaxLength(name, 100, "name", "should not be greater than 100 characters");
        this.assertArgumentGreaterOrEquals(age, 18, "age", "should not be less than 18 years");
    }
}
```