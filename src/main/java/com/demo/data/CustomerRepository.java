package com.demo.data;

import com.demo.model.Customer;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {
    Customer getById(String customerId);
}