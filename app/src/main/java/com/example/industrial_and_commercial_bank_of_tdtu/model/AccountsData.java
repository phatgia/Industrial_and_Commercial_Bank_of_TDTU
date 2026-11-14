package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu /api/accounts/list.php.
 */
public class AccountsData {
    @SerializedName("policy")
    private BankPolicy policy;

    @SerializedName("accounts")
    private List<BankAccount> accounts;

    public BankPolicy getPolicy() { return policy; }
    public List<BankAccount> getAccounts() { return accounts; }
}
