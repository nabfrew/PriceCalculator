package com.demo.controllers;

import com.demo.dto.PriceResponse;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class ControllerTest {
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