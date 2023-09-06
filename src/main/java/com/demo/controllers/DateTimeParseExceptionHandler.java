package com.demo.controllers;

import io.micronaut.core.annotation.Order;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

import java.time.format.DateTimeParseException;

@Produces
@Singleton
@Order(-1) // Specify the order to ensure it takes precedence
public class DateTimeParseExceptionHandler implements ExceptionHandler<DateTimeParseException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, DateTimeParseException exception) {
        return HttpResponse.badRequest("Bad Request: " + exception.getMessage());
    }
}