package com.demo;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.inject.Singleton;
import jakarta.persistence.Entity;

@OpenAPIDefinition(
        info = @Info(
                title = "pricecalculator",
                version = "0.0"
        )
)
@Singleton
@Introspected(packages="com.demo", includedAnnotations=Entity.class)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}