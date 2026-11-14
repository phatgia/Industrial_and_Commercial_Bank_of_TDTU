package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu /api/transactions/history.php.
 */
public class TransactionsData {
    @SerializedName("transactions")
    private List<TransactionItem> transactions;

    public List<TransactionItem> getTransactions() { return transactions; }
}
