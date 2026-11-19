package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload tao khach hang moi gui len POST /api/officer/customers.php.
 */
public class CreateCustomerRequest {
    @SerializedName("full_name")
    private final String fullName;

    @SerializedName("phone")
    private final String phone;

    @SerializedName("email")
    private final String email;

    @SerializedName("password")
    private final String password;

    @SerializedName("initial_balance")
    private final double initialBalance;

    public CreateCustomerRequest(String fullName, String phone, String email, String password, double initialBalance) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.initialBalance = initialBalance;
    }
}
