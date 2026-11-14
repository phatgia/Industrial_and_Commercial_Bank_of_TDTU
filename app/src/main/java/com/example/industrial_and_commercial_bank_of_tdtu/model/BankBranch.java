package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin chi nhanh ngan hang phuc vu chuc nang Ban do.
 */
public class BankBranch {
    @SerializedName("id")
    private int id;

    @SerializedName("branch_name")
    private String branchName;

    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("opening_hours")
    private String openingHours;

    public int getId() { return id; }
    public String getBranchName() { return branchName; }
    public String getAddress() { return address; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getOpeningHours() { return openingHours; }
}
