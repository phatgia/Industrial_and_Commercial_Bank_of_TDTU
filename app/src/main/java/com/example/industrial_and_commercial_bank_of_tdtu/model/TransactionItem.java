package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin 1 giao dich trong lich su giao dich.
 */
public class TransactionItem {
    @SerializedName("id")
    private int id;

    @SerializedName("from_account_id")
    private int fromAccountId;

    @SerializedName("from_account_number")
    private String fromAccountNumber;

    @SerializedName("from_account_type")
    private String fromAccountType;

    @SerializedName("to_account_number")
    private String toAccountNumber;

    @SerializedName("to_bank_name")
    private String toBankName;

    @SerializedName("tx_type")
    private String txType;

    @SerializedName("amount")
    private double amount;

    @SerializedName("status")
    private String status;

    @SerializedName("required_ekyc")
    private boolean requiredEkyc;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public int getFromAccountId() { return fromAccountId; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getFromAccountType() { return fromAccountType; }
    public String getToAccountNumber() { return toAccountNumber; }
    public String getToBankName() { return toBankName; }
    public String getTxType() { return txType; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public boolean isRequiredEkyc() { return requiredEkyc; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
}
