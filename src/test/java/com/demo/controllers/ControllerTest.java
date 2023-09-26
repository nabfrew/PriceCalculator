package com.demo.controllers;

import com.demo.calculator.Calculator;
import com.demo.dto.PriceResponse;
import com.demo.model.Customer;
import com.demo.model.DateRange;
import com.demo.model.Discount;
import com.demo.model.Tier;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class ControllerTest {
    private final double TIER_B_PRICE = 0.24;
    private final double TIER_C_PRICE = 0.4;

    private BlockingHttpClient blockingClient;

    @Inject
    @Client("/")
    HttpClient client;

    @BeforeEach
    void setup() {
        blockingClient = client.toBlocking();
    }

    @Test
    void testGetPrice() {
        var startDate = LocalDate.of(2018, 1, 1);

        var freePeriod = 200;
        var discountRate = 0.7;
        var discountDays = 439;
        var discountWeekdays = 313;
        var expectedTierB = discountRate * discountWeekdays * TIER_B_PRICE;
        var expectedTierC = discountRate * discountDays * TIER_C_PRICE;

        var customer = getCustomer(startDate, freePeriod);

        HttpRequest<Customer> request = HttpRequest.POST("/", customer);
        Customer response = blockingClient.retrieve(request, Customer.class);
        var id = response.getId();

        var price = blockingClient.retrieve(HttpRequest.GET("?id=" + id + "&startDate=2019-09-22&endDate=2019-10-01"), PriceResponse.class).price();

        assertEquals(expectedTierB + expectedTierC, price, 1e-4, String.format("%s", 161.392 -price));
    }

    private Customer getCustomer(LocalDate startDate, int freePeriod) {
        var freeDays = List.of(new DateRange(startDate, startDate.plusDays(freePeriod - 1))); // -1 because first day is included.
        var discount30 = new Discount(0.7, List.of(new DateRange(startDate.plusDays(freePeriod), null)));


        var tierB = new Tier(TIER_B_PRICE, List.of(new DateRange(startDate, null)), List.of(discount30), false);
        var tierC = new Tier(TIER_C_PRICE, List.of(new DateRange(startDate, null)), List.of(discount30), true);
        return new Customer(List.of(tierB, tierC), freeDays);
    }
}