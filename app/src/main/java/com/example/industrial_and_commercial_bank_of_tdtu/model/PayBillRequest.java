package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui len /api/utilities/pay.php.
 */
public class PayBillRequest {
    @SerializedName("bill_id")
    private final Integer billId;

    @SerializedName("bill_code")
    private final String billCode;

    public PayBillRequest(Integer billId, String billCode) {
        this.billId = billId;
        this.billCode = billCode;
    }
}
