package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin 1 hoa don tra ve tu /api/utilities/bills.php.
 */
public class UtilityBillApiItem {
    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("service_type")
    private String serviceType;

    @SerializedName("provider_name")
    private String providerName;

    @SerializedName("bill_code")
    private String billCode;

    @SerializedName("amount")
    private double amount;

    @SerializedName("is_paid")
    private boolean isPaid;

    @SerializedName("due_date")
    private String dueDate;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getServiceType() { return serviceType; }
    public String getProviderName() { return providerName; }
    public String getBillCode() { return billCode; }
    public double getAmount() { return amount; }
    public boolean isPaid() { return isPaid; }
    public String getDueDate() { return dueDate; }
    public String getCreatedAt() { return createdAt; }
}
