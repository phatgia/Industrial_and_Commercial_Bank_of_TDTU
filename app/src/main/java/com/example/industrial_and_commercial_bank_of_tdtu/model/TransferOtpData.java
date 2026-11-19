package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/transactions/transfer.php khi action = "request_otp".
 */
public class TransferOtpData {
    @SerializedName("tx_id")
    private int txId;

    @SerializedName("otp_code")
    private String otpCode;

    @SerializedName("required_ekyc")
    private boolean requiredEkyc;

    public int getTxId() { return txId; }
    public String getOtpCode() { return otpCode; }
    public boolean isRequiredEkyc() { return requiredEkyc; }
}
