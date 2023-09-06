package com.demo.controllers;

import com.demo.calculator.Calculator;
import com.demo.data.PriceCalculatorRepository;
import com.demo.dto.PriceResponse;
import com.demo.model.DateRange;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import jakarta.inject.Inject;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@io.micronaut.http.annotation.Controller("/calculate")
public class Controller {

    private final PriceCalculatorRepository repository;

    @Inject
    public Controller(PriceCalculatorRepository repository) {
        this.repository = repository;
    }

    @Get
    @Produces(APPLICATION_JSON)
    public PriceResponse calculatePrice(@QueryValue String customerId, @QueryValue String startDate, @QueryValue String endDate) {
        DateRange queryDateRange;
        try {
            queryDateRange = new DateRange(LocalDate.parse(startDate), LocalDate.parse(endDate)); //
        } catch (DateTimeParseException ex) {
            throw new DateTimeException("Expected date format is ISO_LOCAL_DATE");
        }
        var tiers = repository.getTiersByCustomerId(customerId);

        return new PriceResponse(Calculator.price(tiers, queryDateRange));
    }
}
