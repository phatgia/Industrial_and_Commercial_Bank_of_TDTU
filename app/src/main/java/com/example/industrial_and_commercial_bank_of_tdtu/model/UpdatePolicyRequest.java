package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload cap nhat lai suat tiet kiem gui len POST /api/officer/update_policy.php.
 */
public class UpdatePolicyRequest {
    @SerializedName("saving_interest_rate")
    private final double savingInterestRate;

    @SerializedName("high_value_threshold")
    private final double highValueThreshold;

    public UpdatePolicyRequest(double savingInterestRate, double highValueThreshold) {
        this.savingInterestRate = savingInterestRate;
        this.highValueThreshold = highValueThreshold;
    }
}
