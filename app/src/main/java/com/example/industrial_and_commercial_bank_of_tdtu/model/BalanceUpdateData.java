package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu deposit_withdraw.php va utilities/pay.php.
 */
public class BalanceUpdateData {
    @SerializedName("tx_id")
    private int txId;

    @SerializedName("new_balance")
    private double newBalance;

    @SerializedName("bill_code")
    private String billCode;

    @SerializedName("amount")
    private double amount;

    public int getTxId() { return txId; }
    public double getNewBalance() { return newBalance; }
    public String getBillCode() { return billCode; }
    public double getAmount() { return amount; }
}
