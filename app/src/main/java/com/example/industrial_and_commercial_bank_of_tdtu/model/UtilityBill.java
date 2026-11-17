package com.example.industrial_and_commercial_bank_of_tdtu.model;

import java.io.Serializable;

public class UtilityBill implements Serializable {
    private String customerId;
    private String customerName;
    private String provider;
    private String period;
    private double amount;
    private String dueDate;
    private boolean isPaid;

    public UtilityBill(String customerId, String customerName, String provider, String period, double amount, String dueDate, boolean isPaid) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.provider = provider;
        this.period = period;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isPaid = isPaid;
    }

    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getProvider() { return provider; }
    public String getPeriod() { return period; }
    public double getAmount() { return amount; }
    public String getDueDate() { return dueDate; }
    public boolean isPaid() { return isPaid; }
}