package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomerSummary;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomersData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoard extends AppCompatActivity {

    private TextView btnViewAllKYC;
    private TextView tvCustomerCount, tvCustomerBadge, tvTodayTxCount, tvTodayTxVolume, tvPendingEkycCount, tvPendingStatus;
    private View rowKyc1, rowKyc2, kycDivider;
    private TextView tvKycAvatar1, tvKycName1, tvKycCode1, tvKycAvatar2, tvKycName2, tvKycCode2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        btnViewAllKYC = findViewById(R.id.btnViewAllKYC);
        tvCustomerCount = findViewById(R.id.tvCustomerCount);
        tvCustomerBadge = findViewById(R.id.tvCustomerBadge);
        tvTodayTxCount = findViewById(R.id.tvTodayTxCount);
        tvTodayTxVolume = findViewById(R.id.tvTodayTxVolume);
        tvPendingEkycCount = findViewById(R.id.tvPendingEkycCount);
        tvPendingStatus = findViewById(R.id.tvPendingStatus);

        rowKyc1 = findViewById(R.id.rowKyc1);
        rowKyc2 = findViewById(R.id.rowKyc2);
        kycDivider = findViewById(R.id.kycDivider);
        tvKycAvatar1 = findViewById(R.id.tvKycAvatar1);
        tvKycName1 = findViewById(R.id.tvKycName1);
        tvKycCode1 = findViewById(R.id.tvKycCode1);
        tvKycAvatar2 = findViewById(R.id.tvKycAvatar2);
        tvKycName2 = findViewById(R.id.tvKycName2);
        tvKycCode2 = findViewById(R.id.tvKycCode2);

        View.OnClickListener openCustomerManager = v -> startActivity(new Intent(DashBoard.this, CustomerManager.class));
        if (btnViewAllKYC != null) btnViewAllKYC.setOnClickListener(openCustomerManager);
        if (rowKyc1 != null) rowKyc1.setOnClickListener(openCustomerManager);
        if (rowKyc2 != null) rowKyc2.setOnClickListener(openCustomerManager);

        View btnNoti = findViewById(R.id.btnNoti);
        if (btnNoti != null) {
            btnNoti.setOnClickListener(v -> startActivity(new Intent(DashBoard.this, ManagerProfile.class)));
        }

        View btnAdd = findViewById(R.id.btnAdd);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> startActivity(new Intent(DashBoard.this, SystemConfig.class)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void loadDashboardData() {
        ApiClient.getService().getCustomers().enqueue(new Callback<ApiResponse<CustomersData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomersData>> call,
                                   @NonNull Response<ApiResponse<CustomersData>> response) {
                ApiResponse<CustomersData> body = response.body();
                if (!response.isSuccessful() || body == null || !body.isSuccess() || body.getData() == null) {
                    return;
                }

                CustomersData data = body.getData();
                List<CustomerSummary> customers = data.getCustomers() != null ? data.getCustomers() : new ArrayList<>();
                CustomersData.DashboardStats stats = data.getStats();

                int totalCustomers = stats != null ? stats.getTotalCustomers() : customers.size();
                int todayTxCount = stats != null ? stats.getTodayTxCount() : 0;
                double todayTxVolume = stats != null ? stats.getTodayTxVolume() : 0.0;
                int pendingCount = stats != null ? stats.getPendingEkycCount() : 0;
                double savingRate = stats != null ? stats.getSavingInterestRate() : 5.5;

                if (tvCustomerCount != null) tvCustomerCount.setText(String.valueOf(totalCustomers));
                if (tvCustomerBadge != null) tvCustomerBadge.setText(String.format(Locale.US, "Lãi %.1f%%/năm", savingRate));
                if (tvTodayTxCount != null) tvTodayTxCount.setText(String.valueOf(todayTxCount));
                if (tvTodayTxVolume != null) tvTodayTxVolume.setText(formatShortVnd(todayTxVolume));
                if (tvPendingEkycCount != null) tvPendingEkycCount.setText(String.format(Locale.US, "%02d", pendingCount));
                if (tvPendingStatus != null) tvPendingStatus.setText(pendingCount > 0 ? "Ưu tiên" : "Đã xử lý");

                List<CustomerSummary> recentList = new ArrayList<>();
                for (CustomerSummary c : customers) {
                    if (!c.isEkycVerified()) recentList.add(c);
                }
                if (recentList.isEmpty()) {
                    recentList.addAll(customers);
                }
                bindKycRow(rowKyc1, tvKycAvatar1, tvKycName1, tvKycCode1, recentList.size() > 0 ? recentList.get(0) : null);
                bindKycRow(rowKyc2, tvKycAvatar2, tvKycName2, tvKycCode2, recentList.size() > 1 ? recentList.get(1) : null);
                if (kycDivider != null) {
                    kycDivider.setVisibility(recentList.size() > 1 ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomersData>> call, @NonNull Throwable t) {
                Toast.makeText(DashBoard.this, "Không tải được thống kê: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindKycRow(View row, TextView avatar, TextView name, TextView code, CustomerSummary c) {
        if (row == null) return;
        if (c == null) {
            row.setVisibility(View.GONE);
            return;
        }
        row.setVisibility(View.VISIBLE);
        String fullName = c.getFullName() != null ? c.getFullName().trim() : "Khách hàng";
        if (name != null) name.setText(fullName);
        if (code != null) {
            String status = c.isEkycVerified() ? "Đã eKYC" : "Chưa eKYC";
            code.setText(String.format(Locale.US, "KH-%06d • %s", c.getId(), status));
        }
        if (avatar != null) {
            avatar.setText(extractInitials(fullName));
        }
    }

    private String extractInitials(String fullName) {
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) return "KH";
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase(Locale.ROOT);
        String first = parts[0].substring(0, 1);
        String last = parts[parts.length - 1].substring(0, 1);
        return (first + last).toUpperCase(Locale.ROOT);
    }

    private String formatShortVnd(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format(Locale.forLanguageTag("vi-VN"), "%.1f tỷ", amount / 1_000_000_000.0);
        }
        if (amount >= 1_000_000) {
            return String.format(Locale.forLanguageTag("vi-VN"), "%.1f tr", amount / 1_000_000.0);
        }
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        return nf.format(Math.round(amount)) + " đ";
    }
}