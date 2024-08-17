package com.kaua.ecommerce.lib.infrastructure.clients;

import com.kaua.ecommerce.lib.domain.exceptions.*;
import com.kaua.ecommerce.lib.domain.validation.Error;
import com.kaua.ecommerce.lib.infrastructure.configurations.json.Json;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ConflictException;
import com.kaua.ecommerce.lib.infrastructure.exceptions.ForbiddenException;
import com.kaua.ecommerce.lib.infrastructure.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface HttpClientHandlers {

    /**
     * Returns the namespace that is being accessed.
     * <p>Example: <code>return "users-ms";</code></p>
     * <p>Example: <code>return "products-ms";</code></p>
     *
     * @return {@link String}
     **/
    String namespace();

    /**
     * Used to log information about the response handlers.
     * <p>Example: <code>return LoggerFactory.getLogger(UserRestClient.class);</code></p>
     *
     * @return {@link Logger}
     **/
    Logger logger();

    /**
     * Handles the not found response from the client.
     * <p>Example: <code>return response -> Mono.error(NotFoundException.with("Not found observed from %s [resourceId:%s]".formatted(namespace(), id)));</code></p>
     *
     * @param id {@link String}
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> notFoundHandler(final String id) {
        return response -> Mono.error(NotFoundException
                .with("Not found observed from %s [resourceId:%s]"
                        .formatted(namespace(), id)));
    }

    /**
     * Handles the forbidden response from the client with an id and action parameters.
     * <p>Example: <code>return response -> Mono.error(ForbiddenException.with("Forbidden observed during %s from %s [method:%s] [resourceId:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, aResponse)));</code></p>
     * <p>Example: <code>return response -> Mono.error(ForbiddenException.with("Forbidden observed from %s [method:%s] [resourceId:%s] [response:%s]".formatted(namespace(), aMethodName, id, aResponse)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> forbiddenHandler(final String id, final String... actionParam) {
        return response -> handleClientError(
                response,
                id,
                "Forbidden observed",
                (errorResponse) -> ForbiddenException.with(errorResponse.errors().isEmpty() ? errorResponse.message() : errorResponse.errors().get(0).message()),
                actionParam
        );
    }

    /**
     * Handles the unauthorized response from the client with an id and action parameters.
     * <p>Example: <code>return response -> Mono.error(UnauthorizedException.with("Unauthorized observed during %s from %s [method:%s] [resourceId:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, aResponse)));</code></p>
     * <p>Example: <code>return response -> Mono.error(UnauthorizedException.with("Unauthorized observed from %s [method:%s] [resourceId:%s] [response:%s]".formatted(namespace(), aMethodName, id, aResponse)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> unauthorizedHandler(final String id, final String... actionParam) {
        return response -> handleClientError(
                response,
                id,
                "Unauthorized observed",
                (errorResponse) -> UnauthorizedException.with(errorResponse.errors().isEmpty() ? errorResponse.message() : errorResponse.errors().get(0).message()),
                actionParam
        );
    }

    /**
     * Handles the conflict response from the client with an id and action parameters.
     * <p>Example: <code>return response -> Mono.error(ConflictException.with("Conflict observed during %s from %s [method:%s] [resourceId:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, aResponse)));</code></p>
     * <p>Example: <code>return response -> Mono.error(ConflictException.with("Conflict observed from %s [method:%s] [resourceId:%s] [response:%s]".formatted(namespace(), aMethodName, id, aResponse)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> conflictHandler(final String id, final String... actionParam) {
        return response -> handleClientError(
                response,
                id,
                "Conflict observed",
                (errorResponse) -> ConflictException.with(errorResponse.errors().isEmpty() ? errorResponse.message() : errorResponse.errors().get(0).message()),
                actionParam
        );
    }

    /**
     * Handles the bad request response from the client with an id and action parameters.
     * <p>Example: <code>return response -> responseBody.flatMap(body -> Mono.error(ValidationException.with("Bad request observed during %s from %s [method:%s] [resourceId:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, body)));</code></p>
     * <p>Example: <code>return response -> responseBody.flatMap(body -> Mono.error(ValidationException.with("Bad request observed from %s [method:%s] [resourceId:%s] [response:%s]".formatted(namespace(), aMethodName, id, body)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> badRequestHandler(final String id, final String... actionParam) {
        return response -> handleClientError(
                response,
                id,
                "Bad request observed",
                (errorResponse) -> ValidationException.with(errorResponse.errors()),
                actionParam
        );
    }

    /**
     * Handles unprocessable entity response from the client with an id and action parameters.
     * <p>Example: <code>return response -> responseBody.flatMap(body -> Mono.error(DomainException.with("Unprocessable entity observed during %s from %s [method:%s] [resourceId:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, body)));</code></p>
     * <p>Example: <code>return response -> responseBody.flatMap(body -> Mono.error(DomainException.with("Unprocessable entity observed from %s [method:%s] [resourceId:%s] [response:%s]".formatted(namespace(), aMethodName, id, body)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> unprocessableEntityHandler(final String id, final String... actionParam) {
        return response -> handleClientError(
                response,
                id,
                "Unprocessable entity observed",
                (errorResponse) -> DomainException.with(errorResponse.errors()),
                actionParam
        );
    }

    /**
     * Handles the 5xx response from the client with an id and action parameters.
     * <p>Example: <code>return response -> Mono.error(InternalErrorException.with("Error observed during %s from %s [method:%s] [resourceId:%s] [status:%s] [response:%s]".formatted(action, namespace(), aMethodName, id, aStatus, aResponse)));</code></p>
     * <p>Example: <code>return response -> Mono.error(InternalErrorException.with("Error observed from %s [method:%s] [resourceId:%s] [status:%s] [response:%s]".formatted(namespace(), aMethodName, id, aStatus, aResponse)));</code></p>
     *
     * @param id          {@link String}
     * @param actionParam {@link String} Optional parameter with the action that was being performed.
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     **/
    default Function<ClientResponse, Mono<? extends Throwable>> a5xxHandler(final String id, final String... actionParam) {
        return response -> {
            final var aMethodName = response.request().getMethod().name();
            final var aStatus = response.statusCode().value();
            final var aResponse = response.bodyToMono(String.class);

            final var aAction = Optional.ofNullable(actionParam.length > 0 ? actionParam[0] : null);

            return aResponse.flatMap(aResp -> {
                final var aMessage = aAction.map(action -> "Error observed during %s from %s [method:%s] [resourceId:%s] [status:%s] [response:%s]"
                                .formatted(action, namespace(), aMethodName, id, aStatus, aResp))
                        .orElse("Error observed from %s [method:%s] [resourceId:%s] [status:%s] [response:%s]"
                                .formatted(namespace(), aMethodName, id, aStatus, aResp));

                logger().info(aMessage);

                return Mono.error(InternalErrorException.with(aMessage, aStatus));
            }).switchIfEmpty(Mono.defer(() -> handleErrorWithoutResponse(
                    "Error observed",
                    aMethodName,
                    id,
                    (errorResponse -> InternalErrorException.with(errorResponse.message())),
                    actionParam))).cast(Throwable.class);
        };
    }

    private <E extends NoStacktraceException> Mono<? extends Throwable> handleClientError(
            final ClientResponse response,
            final String id,
            final String errorType,
            final Function<ErrorResponse, E> exceptionSupplier,
            final String... actionParam
    ) {
        final var aMethodName = response.request().getMethod().name();
        final var aResponse = response.bodyToMono(String.class);

        return aResponse.flatMap(aResp -> {
            final var aError = Optional.ofNullable(convertToErrorResponse(aResp))
                    .orElse(new ErrorResponse("%s from %s"
                            .formatted(errorType, namespace()), List.of(new Error(aResp))));

            final var aMessage = createBodyErrorMessage(
                    errorType,
                    id,
                    aMethodName,
                    aError,
                    actionParam
            );

            logger().info(aMessage);

            return Mono.error(exceptionSupplier.apply(aError));
        }).switchIfEmpty(Mono.defer(() -> handleErrorWithoutResponse(errorType, aMethodName, id, exceptionSupplier, actionParam))).cast(Throwable.class);
    }

    private <E extends NoStacktraceException> Mono<? extends Throwable> handleErrorWithoutResponse(
            final String errorType,
            final String aMethodName,
            final String id,
            final Function<ErrorResponse, E> exceptionSupplier,
            final String... actionParam
    ) {
        final var aAction = Optional.ofNullable(actionParam.length > 0 ? actionParam[0] : null);
        final var aMessage = aAction.map(action -> "%s during %s from %s [method:%s] [resourceId:%s]"
                        .formatted(errorType, action, namespace(), aMethodName, id))
                .orElseGet(() -> "%s from %s [method:%s] [resourceId:%s]"
                        .formatted(errorType, namespace(), aMethodName, id));

        logger().info(aMessage);

        return Mono.error(exceptionSupplier.apply(new ErrorResponse(aMessage, List.of())));
    }

    private String createBodyErrorMessage(final String errorType, final String id, final String aMethodName, final ErrorResponse aResponse, final String... actionParam) {
        final var aAction = Optional.ofNullable(actionParam.length > 0 ? actionParam[0] : null);

        return aAction.map(act -> "%s during %s from %s [method:%s] [resourceId:%s] [response:%s]"
                        .formatted(errorType, act, namespace(), aMethodName, id, aResponse))
                .orElse("%s from %s [method:%s] [resourceId:%s] [response:%s]"
                        .formatted(errorType, namespace(), aMethodName, id, aResponse));
    }

    private ErrorResponse convertToErrorResponse(final String body) {
        try {
            final var aErrorResponse = Json.readTree(body, ErrorResponse.class);

            if (aErrorResponse.message() == null || aErrorResponse.errors() == null) {
                return null;
            }

            return aErrorResponse;
        } catch (final Exception e) {
            return null;
        }
    }

    record ErrorResponse(String message, List<Error> errors) {
    }
}