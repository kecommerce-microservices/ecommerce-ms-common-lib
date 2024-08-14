package com.kaua.ecommerce.lib.infrastructure.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.kaua.ecommerce.lib.domain.exceptions.DomainException;
import com.kaua.ecommerce.lib.domain.exceptions.InternalErrorException;
import com.kaua.ecommerce.lib.domain.exceptions.NotFoundException;
import com.kaua.ecommerce.lib.domain.utils.IdentifierUtils;
import com.kaua.ecommerce.lib.infrastructure.Main;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(classes = {Main.class})
@Tag("integrationTest")
public class HttpClientUtilsTest {

    @Autowired
    private ObjectMapper mapper;

    @Value("${web-client.base-url}")
    private String url;

    @BeforeEach
    void clean() {
        WireMock.reset();
        WireMock.resetAllRequests();
    }

    @Test
    void testSuccessDoGet() {
        final var aId = IdentifierUtils.generateNewId();

        stubFor(
                get("/v3/test")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aResponse = Assertions.assertDoesNotThrow(
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/v3/test")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertTrue(aResponse.isPresent());
        Assertions.assertEquals("Teste", aResponse.get());
    }

    @Test
    void testBadRequestHandler() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Bad request observed from HttpClientUtilsImpl [method:GET] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                get("/bad-request")
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/bad-request")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testNotFoundHandler() {
        final var aId = IdentifierUtils.generateNewId();

        stubFor(
                get("/not-found")
                        .willReturn(aResponse()
                                .withStatus(404)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aResponse = Assertions.assertDoesNotThrow(
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/not-found")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertTrue(aResponse.isEmpty());
    }

    @Test
    void testUnprocessableEntityHandler() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Unprocessable entity observed from HttpClientUtilsImpl [method:GET] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                get("/unprocessable-entity")
                        .willReturn(aResponse()
                                .withStatus(422)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/unprocessable-entity")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void test5xxHandler() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl [method:GET] [resourceId:%s] [status:500]"
                .formatted(aId);

        stubFor(
                get("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/5xx")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testWebClientExceptionUnexpected() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        stubFor(
                get("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/5xx")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testUnexpectedException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Unhandled error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        final var aClient = new HttpClientUtilsImpl();

        Supplier<String> aSupplier = () -> {
            throw new RuntimeException("Unexpected error");
        };

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doGet(aId, aSupplier));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testConnectionTimeoutException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "ConnectionTimeout error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        final var aWebClient = WebClient.builder()
                .baseUrl("http://localhost:1234")
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/v3/test")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testReadTimeoutException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Timeout error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        stubFor(
                get("/read-timeout")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(5000)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doGet(aId, () -> aWebClient.get()
                        .uri("/read-timeout")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testPostBadRequestHandler() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Bad request observed from HttpClientUtilsImpl [method:POST] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                post("/bad-request")
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doPost(aId, () -> aWebClient.post()
                        .uri("/bad-request")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testPostUnprocessableEntityHandler() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Unprocessable entity observed from HttpClientUtilsImpl [method:POST] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                post("/unprocessable-entity")
                        .willReturn(aResponse()
                                .withStatus(422)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doPost(aId, () -> aWebClient.post()
                        .uri("/unprocessable-entity")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testPostThrowWebClientException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl [method:POST] [resourceId:%s] [status:500]"
                .formatted(aId);

        stubFor(
                post("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(aId, () -> aWebClient.post()
                        .uri("/5xx")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testPostWriteTimeoutException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Timeout error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        stubFor(
                post("/write-timeout")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(5000)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(aId, () -> aWebClient.post()
                        .uri("/write-timeout")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostSuccess() {
        final var aId = IdentifierUtils.generateNewId();

        stubFor(
                post("/v3/test")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aResponse = Assertions.assertDoesNotThrow(
                () -> aClient.doPost(aId, () -> aWebClient.post()
                        .uri("/v3/test")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals("Teste", aResponse);
    }

    @Test
    void testDoPostWithoutIdReadTimeoutException() {
        final var expectedErrorMessage = "Timeout error observed from HttpClientUtilsImpl on making a POST request";

        stubFor(
                post("/write-timeout")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(5000)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/write-timeout")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostWithoutIdWebClientUnexpectedException() {
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl on making a POST request";

        stubFor(
                post("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/5xx")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostWithoutIdUnexpectedException() {
        final var expectedErrorMessage = "Unhandled error observed from HttpClientUtilsImpl on making a POST request";

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(() -> {
                    throw new RuntimeException("Unexpected error");
                }));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostWithoutIdSuccess() {
        stubFor(
                post("/v3/test")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aResponse = Assertions.assertDoesNotThrow(
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/v3/test")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals("Teste", aResponse);
    }

    @Test
    void testDoPostWithoutIdConnectionTimeoutException() {
        final var expectedErrorMessage = "ConnectionTimeout error observed from HttpClientUtilsImpl on making a POST request";

        final var aWebClient = WebClient.builder()
                .baseUrl("http://localhost:1234")
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/v3/test")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostWithoutIdBadRequestHandler() {
        final var expectedErrorMessage = "Bad request observed from HttpClientUtilsImpl [method:POST] [resourceId:null] [body:Teste]";

        stubFor(
                post("/bad-request")
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/bad-request")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoPostWithoutId5xxHandler() {
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl [method:POST] [resourceId:null] [status:500]";

        stubFor(
                post("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doPost(() -> aWebClient.post()
                        .uri("/5xx")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(null))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(null))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(null))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoUpdateNotFound() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Not found observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        stubFor(
                put("/not-found")
                        .willReturn(aResponse()
                                .withStatus(404)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(NotFoundException.class,
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/not-found")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoUpdateBadRequest() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Bad request observed from HttpClientUtilsImpl [method:PUT] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                put("/bad-request")
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/bad-request")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoUpdateUnprocessableEntity() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Unprocessable entity observed from HttpClientUtilsImpl [method:PUT] [resourceId:%s] [body:Teste]"
                .formatted(aId);

        stubFor(
                put("/unprocessable-entity")
                        .willReturn(aResponse()
                                .withStatus(422)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(DomainException.class,
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/unprocessable-entity")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoUpdate5xx() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Error observed from HttpClientUtilsImpl [method:PUT] [resourceId:%s] [status:500]"
                .formatted(aId);

        stubFor(
                put("/5xx")
                        .willReturn(aResponse()
                                .withStatus(500)
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/5xx")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isNotFound, aClient.notFoundHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    @Test
    void testDoUpdateSuccess() {
        final var aId = IdentifierUtils.generateNewId();

        stubFor(
                put("/v3/test")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aResponse = Assertions.assertDoesNotThrow(
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/v3/test")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals("Teste", aResponse);
    }

    @Test
    void testDoUpdateReadTimeoutException() {
        final var aId = IdentifierUtils.generateNewId();
        final var expectedErrorMessage = "Timeout error observed from HttpClientUtilsImpl [resourceId:%s]"
                .formatted(aId);

        stubFor(
                put("/read-timeout")
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withFixedDelay(5000)
                                .withBody("Teste")
                        )
        );

        final var aWebClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(createHttpClientConfig()))
                .build();

        final var aClient = new HttpClientUtilsImpl();

        final var aException = Assertions.assertThrows(InternalErrorException.class,
                () -> aClient.doUpdate(aId, () -> aWebClient.put()
                        .uri("/read-timeout")
                        .bodyValue("Teste")
                        .retrieve()
                        .onStatus(aClient.isBadRequest, aClient.badRequestHandler(aId))
                        .onStatus(aClient.isUnprocessableEntity, aClient.unprocessableEntityHandler(aId))
                        .onStatus(aClient.is5xx, aClient.a5xxHandler(aId))
                        .bodyToMono(String.class)
                        .block()));

        Assertions.assertEquals(expectedErrorMessage, aException.getMessage());
    }

    private HttpClient createHttpClientConfig() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Duration.ofSeconds(1).toMillisPart())
                .responseTimeout(Duration.ofSeconds(1))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new WriteTimeoutHandler(2)));
    }

    static class HttpClientUtilsImpl implements HttpClientUtils {

        @Override
        public String namespace() {
            return "HttpClientUtilsImpl";
        }
    }
}
