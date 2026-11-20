package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin trich xuat tu anh mat truoc CCCD qua /api/ekyc/ocr.php.
 */
public class OcrCccdData {
    @SerializedName("cccd_number")
    private String cccdNumber;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("dob")
    private String dob;

    @SerializedName("address")
    private String address;

    public String getCccdNumber() { return cccdNumber; }
    public String getFullName() { return fullName; }
    public String getDob() { return dob; }
    public String getAddress() { return address; }
}
