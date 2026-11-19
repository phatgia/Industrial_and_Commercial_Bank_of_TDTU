package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui len /api/transactions/deposit_withdraw.php.
 */
public class DepositWithdrawRequest {
    @SerializedName("type")
    private final String type;

    @SerializedName("amount")
    private final double amount;

    @SerializedName("description")
    private final String description;

    public DepositWithdrawRequest(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
    }
}
