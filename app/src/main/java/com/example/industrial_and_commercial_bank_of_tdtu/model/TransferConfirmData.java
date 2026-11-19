package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/transactions/transfer.php khi action = "confirm".
 */
public class TransferConfirmData {
    @SerializedName("tx_id")
    private int txId;

    @SerializedName("amount")
    private double amount;

    @SerializedName("status")
    private String status;

    public int getTxId() { return txId; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}
