package com.example.industrial_and_commercial_bank_of_tdtu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Du lieu tra ve tu /api/auth/login.php.
 */
public class LoginData {
    @SerializedName("user")
    private User user;

    public User getUser() { return user; }
}
