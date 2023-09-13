package com.demo.controllers;

import com.demo.calculator.Calculator;
import com.demo.data.CustomerRepository;
import com.demo.dto.PriceResponse;
import com.demo.model.DateRange;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@Controller("/calculate")
public class CalculateController {

    private final CustomerRepository repository;

    public CalculateController(CustomerRepository repository) {
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

        var customer = repository.getById(customerId);
        if (customer == null) {
            return new PriceResponse(0);
        }


        customer.applyFreeDays();

        return new PriceResponse(Calculator.price(customer.getTiers(), queryDateRange));
    }
}
