package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UpdatePolicyRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SystemConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_config);

        EditText etInterest12M = findViewById(R.id.etInterest12M);
        Button btnSaveConfig = findViewById(R.id.btnSaveConfig);

        if (btnSaveConfig != null) {
            btnSaveConfig.setOnClickListener(v -> {
                String rawRate = etInterest12M != null ? etInterest12M.getText().toString().trim() : "";
                double savingRate;
                try {
                    savingRate = Double.parseDouble(rawRate);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Vui lòng nhập lãi suất hợp lệ.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (savingRate < 0 || savingRate > 50) {
                    Toast.makeText(this, "Lãi suất phải từ 0% đến 50%.", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSaveConfig.setEnabled(false);
                UpdatePolicyRequest req = new UpdatePolicyRequest(savingRate, 10000000.0);
                ApiClient.getService().updatePolicy(req).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                           @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                        btnSaveConfig.setEnabled(true);
                        ApiResponse<Map<String, Object>> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            Toast.makeText(SystemConfig.this, "Cập nhật lãi suất thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String msg = ApiErrorHelper.extractMessage(response, "Cập nhật thất bại.");
                            Toast.makeText(SystemConfig.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Throwable t) {
                        btnSaveConfig.setEnabled(true);
                        Toast.makeText(SystemConfig.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}
