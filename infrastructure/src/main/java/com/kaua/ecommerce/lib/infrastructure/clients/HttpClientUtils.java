package com.kaua.ecommerce.lib.infrastructure.clients;

import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.exceptions.NoStacktraceException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClientException;

import java.net.ConnectException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface HttpClientUtils extends HttpClientHandlers {

    Predicate<HttpStatusCode> isNotFound = HttpStatus.NOT_FOUND::equals;

    Predicate<HttpStatusCode> isForbidden = HttpStatus.FORBIDDEN::equals;

    Predicate<HttpStatusCode> isUnauthorized = HttpStatus.UNAUTHORIZED::equals;

    Predicate<HttpStatusCode> isConflict = HttpStatus.CONFLICT::equals;

    Predicate<HttpStatusCode> is5xx = HttpStatusCode::is5xxServerError;

    Predicate<HttpStatusCode> isBadRequest = HttpStatus.BAD_REQUEST::equals;

    Predicate<HttpStatusCode> isUnprocessableEntity = HttpStatus.UNPROCESSABLE_ENTITY::equals;

    /**
     * @param id  - resource id
     * @param fn  - function to be executed
     * @param <T> - return type
     * @return - Optional of T
     * <p> This method is used to handle GET requests </p>
     * <p> It will return an Optional of T, if the request is successful </p>
     * <p> If the request is not successful, it will throw an NoStacktraceException and subclasses and other exceptions throws internal error exception </p>
     **/
    default <T> Optional<T> doGet(final String id, final Supplier<T> fn) {
        try {
            return Optional.ofNullable(fn.get());
        } catch (NotFoundException ex) {
            return Optional.empty();
        } catch (NoStacktraceException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    /**
     * @param id  - resource id
     * @param fn  - function to be executed
     * @param <T> - return type
     * @return - T
     * <p> This method is used to handle POST requests with resource id </p>
     * <p> It will return T, if the request is successful </p>
     * <p> If the request is not successful, it will throw an NoStacktraceException and subclasses and other exceptions throws internal error exception </p>
     **/
    default <T> T doPost(final String id, final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (NoStacktraceException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    /**
     * @param fn  - function to be executed
     * @param <T> - return type
     * @return - T
     * <p> This method is used to handle POST requests </p>
     * <p> It will return T, if the request is successful </p>
     * <p> If the request is not successful, it will throw an NoStacktraceException and subclasses and other exceptions throws internal error exception </p>
     **/
    default <T> T doPost(final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (NoStacktraceException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(ex);
        } catch (Throwable t) {
            throw handleThrowable(t);
        }
    }

    /**
     * @param id  - resource id
     * @param fn  - function to be executed
     * @param <T> - return type
     * @return - T
     * <p> This method is used to handle PUT or PATCH requests </p>
     * <p> It will return T, if the request is successful </p>
     * <p> If the request is not successful, it will throw an NoStacktraceException and subclasses and other exceptions throws internal error exception </p>
     **/
    default <T> T doUpdate(final String id, final Supplier<T> fn) {
        try {
            return fn.get();
        } catch (NoStacktraceException ex) {
            throw ex;
        } catch (WebClientException ex) {
            throw handleWebClientException(id, ex);
        } catch (Throwable t) {
            throw handleThrowable(id, t);
        }
    }

    default <T> void doDelete(final String id, final Supplier<T> fn) {
        try {
            fn.get();
        } catch (NoStacktraceException ex) {
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
            logger().info("ConnectionTimeout error observed from %s [resourceId:%s]".formatted(namespace(), id));
            return InternalErrorException.with("ConnectionTimeout error observed from %s [resourceId:%s]"
                    .formatted(namespace(), id));
        }

        if (cause instanceof ReadTimeoutException || cause instanceof WriteTimeoutException) {
            logger().info("Timeout error observed from %s [resourceId:%s]".formatted(namespace(), id));
            return InternalErrorException.with("Timeout error observed from %s [resourceId:%s]"
                    .formatted(namespace(), id));
        }

        logger().info("Error observed from %s [resourceId:%s]".formatted(namespace(), id), ex);
        return InternalErrorException.with("Error observed from %s [resourceId:%s]"
                .formatted(namespace(), id), ex);
    }

    private InternalErrorException handleThrowable(final String id, final Throwable t) {
        if (t instanceof InternalErrorException ex) {
            return ex;
        }

        logger().error("Unhandled error observed from %s [resourceId:%s]".formatted(namespace(), id), t);
        return InternalErrorException.with("Unhandled error observed from %s [resourceId:%s]".formatted(namespace(), id), t);
    }

    private InternalErrorException handleWebClientException(final WebClientException ex) {
        final var cause = ExceptionUtils.getRootCause(ex);

        if (cause instanceof ConnectException) {
            logger().info("ConnectionTimeout error observed from %s on making request".formatted(namespace()));
            return InternalErrorException.with("ConnectionTimeout error observed from %s on making request"
                    .formatted(namespace()));
        }

        if (cause instanceof ReadTimeoutException || cause instanceof WriteTimeoutException) {
            logger().info("Timeout error observed from %s on making request".formatted(namespace()));
            return InternalErrorException.with("Timeout error observed from %s on making request"
                    .formatted(namespace()));
        }

        logger().info("Error observed from %s on making request".formatted(namespace()), ex);
        return InternalErrorException.with("Error observed from %s on making request"
                .formatted(namespace()), ex);
    }

    private InternalErrorException handleThrowable(final Throwable t) {
        if (t instanceof InternalErrorException ex) {
            return ex;
        }

        logger().error("Unhandled error observed from %s on making request".formatted(namespace()), t);
        return InternalErrorException.with("Unhandled error observed from %s on making request".formatted(namespace()), t);
    }
}