package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Chinh sach lai suat va han muc xac thuc sinh trac hoc eKYC.
 */
public class BankPolicy {
    @SerializedName("saving_interest_rate")
    private double savingInterestRate;

    @SerializedName("high_value_threshold")
    private double highValueThreshold;

    public double getSavingInterestRate() { return savingInterestRate; }
    public double getHighValueThreshold() { return highValueThreshold; }
}
