package com.demo.model;

/**
 * Maybe useful to have to assign to each tier, in order to enforce a unique (per customer) constraint somehow.
 * Or might be good to have for record-keeping purposes.
 * Otherwise, if multiples of the same tier is fine, this enum isn't necessary, because we need a price
 * for each tier for each customer anyway.
 */
public enum TierType {
    A, // Maybe give these a 'default rate' property?
    B,
    C
}
