package com.demo.controllers;

import com.demo.calculator.Calculator;
import com.demo.data.CustomerRepository;
import com.demo.dto.PriceResponse;
import com.demo.model.Customer;
import com.demo.model.DateRange;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.validation.constraints.NotBlank;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static io.micronaut.http.MediaType.APPLICATION_JSON;

@ExecuteOn(TaskExecutors.IO)
@Controller("/")
public class CustomerController {

    final CustomerRepository repository;

    public CustomerController(CustomerRepository repository) {
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

        var customer = repository.findById(customerId).orElseThrow();
        customer.applyFreeDays();

        return new PriceResponse(Calculator.price(customer.getTiers(), queryDateRange));
    }

    @Post
    public HttpResponse<Customer> save(@Body @NotBlank Customer customer) {
        repository.save(customer);

        return HttpResponse
                .created(customer);
    }
}
