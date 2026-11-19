package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin tom tat khach hang tra ve tu /api/officer/customers.php.
 */
public class CustomerSummary {
    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("ekyc_verified")
    private boolean ekycVerified;

    @SerializedName("checking_balance")
    private double checkingBalance;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isEkycVerified() { return ekycVerified; }
    public double getCheckingBalance() { return checkingBalance; }
    public String getCreatedAt() { return createdAt; }
}
