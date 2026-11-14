package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Payload gui len API dang nhap (/api/auth/login.php).
 */
public class LoginRequest {
    @SerializedName("identifier")
    private final String identifier;

    @SerializedName("password")
    private final String password;

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }
}
