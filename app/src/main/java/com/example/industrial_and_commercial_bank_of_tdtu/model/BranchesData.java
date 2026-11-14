package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu /api/branches/list.php.
 */
public class BranchesData {
    @SerializedName("branches")
    private List<BankBranch> branches;

    public List<BankBranch> getBranches() { return branches; }
}
