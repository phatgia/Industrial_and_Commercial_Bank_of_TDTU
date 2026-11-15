package com.example.industrial_and_commercial_bank_of_tdtu.network;

import android.content.Context;

import com.example.industrial_and_commercial_bank_of_tdtu.model.User;

/**
 * Luu phien dang nhap trong bo nho RAM (chi ton tai khi app dang mo, tu mat khi out app).
 */
public class SessionManager {

    private static volatile User currentUser;

    public SessionManager() {
    }

    public SessionManager(Context context) {
        // Giu constructor nhan Context de tuong thich voi cac Fragment hien tai
    }

    public void saveUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public int getUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

    public String getFullName() {
        return currentUser != null && currentUser.getFullName() != null ? currentUser.getFullName() : "";
    }

    public String getEmail() {
        return currentUser != null && currentUser.getEmail() != null ? currentUser.getEmail() : "";
    }

    public String getPhone() {
        return currentUser != null && currentUser.getPhone() != null ? currentUser.getPhone() : "";
    }

    public String getRole() {
        return currentUser != null && currentUser.getRole() != null ? currentUser.getRole() : "CUSTOMER";
    }

    public boolean isEkycVerified() {
        return currentUser != null && currentUser.isEkycVerified();
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public void clear() {
        currentUser = null;
    }
}
