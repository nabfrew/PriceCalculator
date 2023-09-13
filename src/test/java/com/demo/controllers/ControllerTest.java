package com.demo.controllers;

import com.demo.dto.PriceResponse;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.micronaut.http.MediaType.APPLICATION_JSON_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;


@MicronautTest
class ControllerTest {

    @Inject
    private EmbeddedServer server;

    @Inject
    @Client("/calculate")
    HttpClient client;

    @BeforeEach
    void  setUp() {
        client = HttpClient.create(server.getURL());
    }

    @Test
    void testGetPrice() {

        HttpRequest<?> request = HttpRequest.GET("?customerId=abc&startDate=0001-01-01&endDate=0001-01-02")
                .accept(APPLICATION_JSON_TYPE);
        var response = client.toBlocking().retrieve(request, PriceResponse.class);

        assertEquals(response, new PriceResponse(0));
    }
}