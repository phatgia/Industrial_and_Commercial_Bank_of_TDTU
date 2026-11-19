package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu GET /api/officer/customers.php.
 */
public class CustomersData {
    @SerializedName("customers")
    private List<CustomerSummary> customers;

    public List<CustomerSummary> getCustomers() { return customers; }
}
