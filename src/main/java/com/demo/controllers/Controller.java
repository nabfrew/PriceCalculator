package com.demo.controllers;

import com.demo.data.CustomerRepository;
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

    private final CustomerRepository repository;

    @Inject
    public Controller(CustomerRepository repository) {
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

        try {
            var customer = repository.getById(customerId);
        } catch (Exception exception) {
            // ignore
        }

        //if (true) {//customer == null) {
            return new PriceResponse(0);
        //}


        //customer.applyFreeDays();

        //var price = Calculator.price(customer.getTiers(), queryDateRange);

        //return new PriceResponse(Calculator.price(customer.getTiers(), queryDateRange));
    }
}
