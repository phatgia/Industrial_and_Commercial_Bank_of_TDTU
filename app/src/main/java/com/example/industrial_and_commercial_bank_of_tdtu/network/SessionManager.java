package com.example.industrial_and_commercial_bank_of_tdtu.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.industrial_and_commercial_bank_of_tdtu.model.User;

/**
 * Luu phien dang nhap cuc bo (userId, fullName, role, ekycVerified) bang SharedPreferences.
 */
public class SessionManager {

    private static final String PREF_NAME = "icbt_bank_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_EKYC_VERIFIED = "ekyc_verified";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        if (user == null) return;
        prefs.edit()
                .putInt(KEY_USER_ID, user.getId())
                .putString(KEY_FULL_NAME, user.getFullName())
                .putString(KEY_EMAIL, user.getEmail())
                .putString(KEY_PHONE, user.getPhone())
                .putString(KEY_ROLE, user.getRole())
                .putBoolean(KEY_EKYC_VERIFIED, user.isEkycVerified())
                .apply();
    }

    public int getUserId() { return prefs.getInt(KEY_USER_ID, -1); }
    public String getFullName() { return prefs.getString(KEY_FULL_NAME, ""); }
    public String getEmail() { return prefs.getString(KEY_EMAIL, ""); }
    public String getPhone() { return prefs.getString(KEY_PHONE, ""); }
    public String getRole() { return prefs.getString(KEY_ROLE, "CUSTOMER"); }
    public boolean isEkycVerified() { return prefs.getBoolean(KEY_EKYC_VERIFIED, false); }
    public boolean isLoggedIn() { return getUserId() > 0; }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
