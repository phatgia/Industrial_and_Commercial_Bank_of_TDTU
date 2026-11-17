package com.example.industrial_and_commercial_bank_of_tdtu.repository;

import com.example.industrial_and_commercial_bank_of_tdtu.model.UtilityBill;
import java.util.HashMap;
import java.util.Map;

public class UtilityMockRepository {

    private static UtilityMockRepository instance;
    private Map<String, UtilityBill> electricityBills;
    private Map<String, UtilityBill> waterBills;

    private UtilityMockRepository() {
        electricityBills = new HashMap<>();
        electricityBills.put("PE012345", new UtilityBill("PE012345", "Trần Gia Phát", "EVN HCMC", "05/2024", 450000, "15/06/2024", false));
        electricityBills.put("PE098765", new UtilityBill("PE098765", "Nguyễn Văn A", "EVN Hà Nội", "05/2024", 1200000, "12/06/2024", false));

        waterBills = new HashMap<>();
        waterBills.put("WA112233", new UtilityBill("WA112233", "Trần Gia Phát", "Cấp nước Gia Định", "05/2024", 125000, "20/06/2024", false));
    }

    public static UtilityMockRepository getInstance() {
        if (instance == null) {
            instance = new UtilityMockRepository();
        }
        return instance;
    }

    public UtilityBill getElectricityBill(String customerId) {
        return electricityBills.get(customerId.toUpperCase());
    }

    public UtilityBill getWaterBill(String customerId) {
        return waterBills.get(customerId.toUpperCase());
    }

    public void markBillAsPaid(String type, String customerId) {
        UtilityBill bill = type.equals("ELECTRICITY") ? getElectricityBill(customerId) : getWaterBill(customerId);
        if (bill != null) {
            // Replace with paid version
            UtilityBill paidBill = new UtilityBill(bill.getCustomerId(), bill.getCustomerName(), bill.getProvider(), bill.getPeriod(), bill.getAmount(), bill.getDueDate(), true);
            if (type.equals("ELECTRICITY")) {
                electricityBills.put(customerId.toUpperCase(), paidBill);
            } else {
                waterBills.put(customerId.toUpperCase(), paidBill);
            }
        }
    }
}