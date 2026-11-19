package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu /api/utilities/bills.php.
 */
public class UtilityBillsData {
    @SerializedName("bills")
    private List<UtilityBillApiItem> bills;

    public List<UtilityBillApiItem> getBills() { return bills; }
}
