package com.demo.data;

import jakarta.inject.Singleton;

@Singleton
public class PriceCalculatorRepositoryFactory {
    public PriceCalculatorRepository createRepository(String databaseType) {
        // Return the appropriate repository based on the databaseType
        if ("cassandra".equalsIgnoreCase(databaseType)) {
            return new CassandraPriceCalculatorRepository();
        } else {
            throw new IllegalArgumentException("Unsupported database type");
        }
    }
}
