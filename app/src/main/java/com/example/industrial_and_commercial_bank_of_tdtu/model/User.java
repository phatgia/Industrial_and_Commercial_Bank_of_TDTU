package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Thong tin nguoi dung (CUSTOMER hoac OFFICER) tra ve tu API.
 */
public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("role")
    private String role;

    @SerializedName("ekyc_face_path")
    private String ekycFacePath;

    @SerializedName("ekyc_verified")
    private boolean ekycVerified;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getEkycFacePath() { return ekycFacePath; }
    public boolean isEkycVerified() { return ekycVerified; }
    public String getCreatedAt() { return createdAt; }
}
