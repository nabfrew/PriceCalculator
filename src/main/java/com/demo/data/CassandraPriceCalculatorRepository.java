package com.demo.data;

import com.demo.model.Tier;
import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CassandraPriceCalculatorRepository implements PriceCalculatorRepository {
    @Override
    public List<Tier> getTiersByCustomerId(String customerId) {
        return new ArrayList<>();
    }
}
