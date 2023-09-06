package com.demo.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable.Serializable
public record PriceResponse(double price) {
}
