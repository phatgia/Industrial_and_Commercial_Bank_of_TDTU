package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/auth/login.php (bao gom Bearer token va thong tin user).
 */
public class LoginData {
    @SerializedName("token")
    private String token;

    @SerializedName("expires_in")
    private int expiresIn;

    @SerializedName("user")
    private User user;

    public String getToken() { return token; }
    public int getExpiresIn() { return expiresIn; }
    public User getUser() { return user; }
}
