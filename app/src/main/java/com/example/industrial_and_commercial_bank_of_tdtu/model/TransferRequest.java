package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui len /api/transactions/transfer.php cho 2 buoc: request_otp va confirm.
 */
public class TransferRequest {
    @SerializedName("action")
    private final String action;

    @SerializedName("to_account_number")
    private final String toAccountNumber;

    @SerializedName("to_bank_name")
    private final String toBankName;

    @SerializedName("amount")
    private final Double amount;

    @SerializedName("description")
    private final String description;

    @SerializedName("tx_id")
    private final Integer txId;

    @SerializedName("otp_code")
    private final String otpCode;

    private TransferRequest(String action,
                            String toAccountNumber,
                            String toBankName,
                            Double amount,
                            String description,
                            Integer txId,
                            String otpCode) {
        this.action = action;
        this.toAccountNumber = toAccountNumber;
        this.toBankName = toBankName;
        this.amount = amount;
        this.description = description;
        this.txId = txId;
        this.otpCode = otpCode;
    }

    public static TransferRequest requestOtp(String toAccountNumber,
                                             String toBankName,
                                             double amount,
                                             String description) {
        return new TransferRequest("request_otp", toAccountNumber, toBankName, amount, description, null, null);
    }

    public static TransferRequest confirm(int txId, String otpCode) {
        return new TransferRequest("confirm", null, null, null, null, txId, otpCode);
    }
}
