package com.demo.controllers;

import com.demo.dto.PriceResponse;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.micronaut.http.client.HttpClient;

import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class ControllerTest {

    @Inject
    private EmbeddedServer server;

    private HttpClient client;

    @BeforeEach
    void  setUp() {
        client = HttpClient.create(server.getURL());
    }

    @Test
    void testGetPrice(RequestSpecification spec) {

        var result = spec.when()
            .accept(JSON)
                .queryParam("customerId", "abc")
                .queryParam("startDate", "0001-01-01")
                .queryParam("endDate", "0001-01-02")
            .get("calculate");

        result.then().statusCode(200);
        assertEquals(result.as(PriceResponse.class), new PriceResponse(0));
    }
}