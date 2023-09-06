package com.demo.data;

import com.demo.model.Tier;

import java.util.List;

public interface PriceCalculatorRepository {
    List<Tier> getTiersByCustomerId(String customerId);
}