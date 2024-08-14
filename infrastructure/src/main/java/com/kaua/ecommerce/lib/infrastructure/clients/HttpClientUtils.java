package com.kaua.ecommerce.lib.infrastructure.clients;

import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.exceptions.ValidationException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface HttpClientUtils {

    Predicate<HttpStatusCode> isNotFound = HttpStatus.NOT_FOUND::equals;

    Predicate<HttpStatusCode> is5xx = HttpStatusCode::is5xxServerError;

    Predicate<HttpStatusCode> isBadRequest = HttpStatus.BAD_REQUEST::equals;

    Predicate<HttpStatusCode> isUnprocessableEntity = HttpStatus.UNPROCESSABLE_ENTITY::equals;

    String namespace();

    default Function<ClientResponse, Mono<? extends Throwable>> badRequestHandler(final String id) {
        return response -> {
            final var responseBody = response.bodyToMono(String.class);

            return responseBody.flatMap(body -> Mono.error(ValidationException
                    .with("Bad request observed from %s [method:%s] [resourceId:%s] [body:%s]"
                            .formatted(namespace(), response.request().getMethod().name(), id, body))));
        };
    }

    default Function<ClientResponse, Mono<? extends Throwable>> notFoundHandler(final String id) {
        return response -> Mono.error(NotFoundException
                .with("Not found observed from %s [resourceId:%s]"
                        .formatted(namespace(), id)));
    }

    default Function<ClientResponse, Mono<? extends Throwable>> unprocessableEntityHandler(final String id) {
        return response -> {
            final var responseBody = response.bodyToMono(String.class);

            return responseBody.flatMap(body -> Mono.error(DomainException
                    .with("Unprocessable entity observed from %s [method:%s] [resourceId:%s] [body:%s]"
                            .formatted(namespace(), response.request().getMethod().name(), id, body))));
        };
    }

    default Function<ClientResponse, Mono<? extends Throwable>> a5xxHandler(final String id) {
        return response -> Mono.error(InternalErrorException
                .with("Error observed from %s [method:%s] [resourceId:%s] [status:%s]"
                        .formatted(namespace(), response.request().getMethod().name(), id, response.statusCode().value())));
    }

    default <T> Optional<T> doGet(final String id, final Supplier<T> fn) {
        try {
            return Optional.ofNullable(fn.get());
        } catch (NotFoundException ex) {
            return Optional.empty();
        } catch (DomainException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    default <T> T doPost(final String id, final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (DomainException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    default <T> T doPost(final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (DomainException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(ex);
        } catch (Throwable t) {
            throw handleThrowable(t);
        }
    }

    default <T> T doUpdate(final String id, final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (NotFoundException | DomainException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    private InternalErrorException handleWebClientException(
            final String id,
            final WebClientException ex
    ) {

        final var cause = ExceptionUtils.getRootCause(ex);

        if (cause instanceof ConnectException) {
            return InternalErrorException.with("ConnectionTimeout error observed from %s [resourceId:%s]"
                    .formatted(namespace(), id));
        }

        if (cause instanceof ReadTimeoutException || cause instanceof WriteTimeoutException) {
            return InternalErrorException.with("Timeout error observed from %s [resourceId:%s]"
                    .formatted(namespace(), id));
        }

        return InternalErrorException.with("Error observed from %s [resourceId:%s]"
                .formatted(namespace(), id), ex);
    }

    private InternalErrorException handleThrowable(final String id, final Throwable t) {
        if (t instanceof InternalErrorException ex) {
            return ex;
        }

        return InternalErrorException.with("Unhandled error observed from %s [resourceId:%s]".formatted(namespace(), id), t);
    }

    private InternalErrorException handleWebClientException(final WebClientException ex) {
        final var cause = ExceptionUtils.getRootCause(ex);

        if (cause instanceof ConnectException) {
            return InternalErrorException.with("ConnectionTimeout error observed from %s on making a POST request"
                    .formatted(namespace()));
        }

        if (cause instanceof ReadTimeoutException || cause instanceof WriteTimeoutException) {
            return InternalErrorException.with("Timeout error observed from %s on making a POST request"
                    .formatted(namespace()));
        }

        return InternalErrorException.with("Error observed from %s on making a POST request"
                .formatted(namespace()), ex);
    }

    private InternalErrorException handleThrowable(final Throwable t) {
        if (t instanceof InternalErrorException ex) {
            return ex;
        }

        return InternalErrorException.with("Unhandled error observed from %s on making a POST request".formatted(namespace()), t);
    }
}