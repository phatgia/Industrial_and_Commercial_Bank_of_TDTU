package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload tao phien thanh toan Stripe Test Mode gui len /api/transactions/stripe_checkout.php.
 */
public class StripeCheckoutRequest {
    @SerializedName("amount")
    private final double amount;

    public StripeCheckoutRequest(double amount) {
        this.amount = amount;
    }
}
