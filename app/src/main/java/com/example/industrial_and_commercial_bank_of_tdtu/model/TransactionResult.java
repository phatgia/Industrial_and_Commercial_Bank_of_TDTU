package com.example.industrial_and_commercial_bank_of_tdtu.model;

import java.io.Serializable;

public class TransactionResult implements Serializable {
    private String transactionId;
    private String date;
    private double amount;
    private String serviceName;
    private String provider;
    private String status;
    private String accountSource;

    public TransactionResult(String transactionId, String date, double amount, String serviceName, String provider, String status, String accountSource) {
        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.serviceName = serviceName;
        this.provider = provider;
        this.status = status;
        this.accountSource = accountSource;
    }

    public String getTransactionId() { return transactionId; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public String getServiceName() { return serviceName; }
    public String getProvider() { return provider; }
    public String getStatus() { return status; }
    public String getAccountSource() { return accountSource; }
}