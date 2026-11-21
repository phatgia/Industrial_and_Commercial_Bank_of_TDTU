package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Du lieu tra ve tu GET /api/officer/customers.php.
 */
public class CustomersData {
    @SerializedName("customers")
    private List<CustomerSummary> customers;

    @SerializedName("stats")
    private DashboardStats stats;

    public List<CustomerSummary> getCustomers() { return customers; }
    public DashboardStats getStats() { return stats; }

    public static class DashboardStats {
        @SerializedName("total_customers")
        private int totalCustomers;

        @SerializedName("today_tx_count")
        private int todayTxCount;

        @SerializedName("today_tx_volume")
        private double todayTxVolume;

        @SerializedName("pending_ekyc_count")
        private int pendingEkycCount;

        @SerializedName("saving_interest_rate")
        private double savingInterestRate;

        public int getTotalCustomers() { return totalCustomers; }
        public int getTodayTxCount() { return todayTxCount; }
        public double getTodayTxVolume() { return todayTxVolume; }
        public int getPendingEkycCount() { return pendingEkycCount; }
        public double getSavingInterestRate() { return savingInterestRate; }
    }
}
