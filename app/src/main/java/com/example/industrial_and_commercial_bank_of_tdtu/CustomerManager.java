package com.example.industrial_and_commercial_bank_of_tdtu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.industrial_and_commercial_bank_of_tdtu.adapter.CustomerAdapter;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CreateCustomerRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomerSummary;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomersData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerManager extends AppCompatActivity {

    private ListView lvCustomers;
    private EditText etSearchCustomer;
    private ImageButton btnAddCustomer;

    private final List<CustomerSummary> allCustomers = new ArrayList<>();
    private final List<CustomerSummary> filteredCustomers = new ArrayList<>();
    private CustomerAdapter customerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manager);

        lvCustomers = findViewById(R.id.lvCustomers);
        etSearchCustomer = findViewById(R.id.etSearchCustomer);
        btnAddCustomer = findViewById(R.id.btnAddCustomer);

        customerAdapter = new CustomerAdapter(this, filteredCustomers);
        if (lvCustomers != null) {
            lvCustomers.setAdapter(customerAdapter);
            lvCustomers.setOnItemClickListener((parent, view, position, id) -> {
                if (position >= 0 && position < filteredCustomers.size()) {
                    showCustomerDialog(false, filteredCustomers.get(position));
                }
            });
        }

        if (etSearchCustomer != null) {
            etSearchCustomer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterCustomers();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (btnAddCustomer != null) {
            btnAddCustomer.setOnClickListener(v -> showCustomerDialog(true, null));
        }

        View btnNoti = findViewById(R.id.btnNoti);
        if (btnNoti != null) {
            btnNoti.setOnClickListener(v -> startActivity(new Intent(CustomerManager.this, ManagerProfile.class)));
        }

        loadCustomers();
    }

    private void loadCustomers() {
        ApiClient.getService().getCustomers().enqueue(new Callback<ApiResponse<CustomersData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<CustomersData>> call,
                                   @NonNull Response<ApiResponse<CustomersData>> response) {
                ApiResponse<CustomersData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    allCustomers.clear();
                    if (body.getData().getCustomers() != null) {
                        allCustomers.addAll(body.getData().getCustomers());
                    }
                    filterCustomers();
                } else {
                    String msg = ApiErrorHelper.extractMessage(response, "Không tải được danh sách khách hàng.");
                    Toast.makeText(CustomerManager.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<CustomersData>> call, @NonNull Throwable t) {
                Toast.makeText(CustomerManager.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterCustomers() {
        String query = etSearchCustomer != null
                ? etSearchCustomer.getText().toString().trim().toLowerCase(Locale.ROOT)
                : "";
        filteredCustomers.clear();
        for (CustomerSummary c : allCustomers) {
            if (query.isEmpty()) {
                filteredCustomers.add(c);
            } else {
                String name = c.getFullName() != null ? c.getFullName().toLowerCase(Locale.ROOT) : "";
                String phone = c.getPhone() != null ? c.getPhone().toLowerCase(Locale.ROOT) : "";
                String email = c.getEmail() != null ? c.getEmail().toLowerCase(Locale.ROOT) : "";
                if (name.contains(query) || phone.contains(query) || email.contains(query)) {
                    filteredCustomers.add(c);
                }
            }
        }
        customerAdapter.notifyDataSetChanged();
    }

    private void showCustomerDialog(boolean isNewCustomer, CustomerSummary existing) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_customer_action);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        EditText etName = dialog.findViewById(R.id.etCustomerName);
        EditText etPhone = dialog.findViewById(R.id.etCustomerPhone);
        EditText etEmail = dialog.findViewById(R.id.etCustomerEmail);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnLock = dialog.findViewById(R.id.btnLock);

        if (isNewCustomer) {
            tvTitle.setText("Thêm khách hàng mới");
            btnLock.setVisibility(View.GONE);
        } else if (existing != null) {
            tvTitle.setText("Cập nhật khách hàng");
            btnLock.setVisibility(View.GONE);
            etName.setText(existing.getFullName());
            etPhone.setText(existing.getPhone());
            etEmail.setText(existing.getEmail());
            btnSave.setText("Cập nhật");
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ Tên, SĐT và Email.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isNewCustomer && existing != null) {
                btnSave.setEnabled(false);
                Map<String, Object> req = new java.util.HashMap<>();
                req.put("action", "update");
                req.put("customer_id", existing.getId());
                req.put("full_name", name);
                req.put("phone", phone);
                req.put("email", email);
                ApiClient.getService().updateCustomer(req).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                           @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                        btnSave.setEnabled(true);
                        ApiResponse<Map<String, Object>> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            Toast.makeText(CustomerManager.this, "Cập nhật khách hàng thành công!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            loadCustomers();
                        } else {
                            String msg = ApiErrorHelper.extractMessage(response, "Không thể cập nhật khách hàng.");
                            Toast.makeText(CustomerManager.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Throwable t) {
                        btnSave.setEnabled(true);
                        Toast.makeText(CustomerManager.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            btnSave.setEnabled(false);
            CreateCustomerRequest req = new CreateCustomerRequest(name, phone, email, "123456", 0);
            ApiClient.getService().createCustomer(req).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Map<String, Object>>> call,
                                       @NonNull Response<ApiResponse<Map<String, Object>>> response) {
                    btnSave.setEnabled(true);
                    ApiResponse<Map<String, Object>> body = response.body();
                    if (response.isSuccessful() && body != null && body.isSuccess()) {
                        Toast.makeText(CustomerManager.this,
                                "Tạo khách hàng thành công (Mật khẩu mặc định: 123456).",
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        loadCustomers();
                    } else {
                        String msg = ApiErrorHelper.extractMessage(response, "Không thể tạo khách hàng.");
                        Toast.makeText(CustomerManager.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<Map<String, Object>>> call, @NonNull Throwable t) {
                    btnSave.setEnabled(true);
                    Toast.makeText(CustomerManager.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
