package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Dai dien cho 1 tai khoan ngan hang: CHECKING, SAVING hoac MORTGAGE.
 */
public class BankAccount {
    @SerializedName("id")
    private int id;

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("account_type")
    private String accountType;

    @SerializedName("balance")
    private double balance;

    @SerializedName("interest_rate")
    private double interestRate;

    @SerializedName("periodic_payment")
    private double periodicPayment;

    @SerializedName("payment_cycle")
    private String paymentCycle;

    @SerializedName("estimated_monthly_profit")
    private double estimatedMonthlyProfit;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public int getUserId() { return userId; }
    public String getAccountType() { return accountType; }
    public double getBalance() { return balance; }
    public double getInterestRate() { return interestRate; }
    public double getPeriodicPayment() { return periodicPayment; }
    public String getPaymentCycle() { return paymentCycle; }
    public double getEstimatedMonthlyProfit() { return estimatedMonthlyProfit; }
    public String getUpdatedAt() { return updatedAt; }
}
