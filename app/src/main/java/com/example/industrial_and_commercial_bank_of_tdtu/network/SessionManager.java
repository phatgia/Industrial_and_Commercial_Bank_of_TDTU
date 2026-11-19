package com.example.industrial_and_commercial_bank_of_tdtu.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Luu phien dang nhap va Bearer Token trong bo nho RAM (tu mat khi out app).
 */
public class SessionManager {

    private static volatile User currentUser;
    private static volatile String authToken;
    private static volatile String checkingAccountNumber;

    public SessionManager() {
    }

    public SessionManager(Context context) {
        // Giu constructor nhan Context de tuong thich voi cac Fragment hien tai
    }

    public void saveSession(User user, String token) {
        currentUser = user;
        authToken = token;
    }

    public void saveUser(User user) {
        currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getToken() {
        return authToken;
    }

    public void setCheckingAccountNumber(String accountNumber) {
        checkingAccountNumber = accountNumber;
    }

    public String getCheckingAccountNumber() {
        return checkingAccountNumber != null ? checkingAccountNumber : "";
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
        return getUserId() > 0 && authToken != null && !authToken.isEmpty();
    }

    public void clear() {
        currentUser = null;
        authToken = null;
        checkingAccountNumber = null;
    }

    /**
     * Thu hoi token tren server (neu dang dang nhap) va xoa phien trong RAM.
     */
    public void logoutAndClear() {
        String token = authToken;
        clear();
        if (token != null && !token.isEmpty()) {
            ApiClient.getService().logout("Bearer " + token).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                    // Best-effort server token revocation
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                    // Phien RAM da duoc xoa san
                }
            });
        }
    }
}
