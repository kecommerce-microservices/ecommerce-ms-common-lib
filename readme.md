
# Ecommerce Common Lib

Uma lib contendo algumas classes que vão ser usadas em outros microserviços

# Índice

- [Features](#features)
- [Instalação](#instalação)
- [Rodando localmente](#rodando-localmente)

# Features

- **Validação**: Inclui handlers como o notification handler e inclui o assertion concer, que é muito utilizado para validar direto no construtor.
- **Eventos**: Fornece uma estrutura de domain events e um domain event publisher.
- **Elementos de DDD**: Contém elementos comuns de Domain-Driven Design como aggregateRoot, entity, identifier e valueobjects.
- **Exceções**: Fornece uma classe genérica de exception (NoStacktraceException), temos dai a domain exception junto com a not found exception.


## Instalação

Precisa ter um personal token e o seu nome de usuário configurado como váriavel de ambiente, para que possa ser possível baixar a lib do github packages.

```groovy
repositories {
    mavenCentral()
    maven {
        name = 'GitHubPackages'
        url = uri('https://maven.pkg.github.com/kecommerce-microservices/ecommerce-ms-common-lib')
        credentials {
            username = System.getenv("GB_USERNAME")
            password = System.getenv("PERSONAL_TOKEN")
        }
    }
}

dependencies {
    implementation 'com.kaua.ecommerce.common-lib.domain:domain:0.2.0'
}
```

## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/kecommerce-microservices/ecommerce-ms-common-lib.git
```

Entre no diretório do projeto

```bash
  cd ecommerce-ms-common-lib
```

Agora abra esse projeto em alguma idea ou então
Instale as dependências

```bash
  ./gradlew --refresh-dependencies
```