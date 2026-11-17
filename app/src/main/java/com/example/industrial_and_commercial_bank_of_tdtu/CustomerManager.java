package com.example.industrial_and_commercial_bank_of_tdtu;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CustomerManager extends AppCompatActivity {

    LinearLayout itemCustomer1, itemCustomer2, itemCustomer3, itemCustomer4;
    ImageButton btnAddCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_manager);

        itemCustomer1 = findViewById(R.id.itemCustomer1);
        itemCustomer2 = findViewById(R.id.itemCustomer2);
        itemCustomer3 = findViewById(R.id.itemCustomer3);
        itemCustomer4 = findViewById(R.id.itemCustomer4);
        btnAddCustomer = findViewById(R.id.btnAddCustomer);

        View.OnClickListener customerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomerDialog(false); // Edit customer
            }
        };

        if (itemCustomer1 != null) itemCustomer1.setOnClickListener(customerClickListener);
        if (itemCustomer2 != null) itemCustomer2.setOnClickListener(customerClickListener);
        if (itemCustomer3 != null) itemCustomer3.setOnClickListener(customerClickListener);
        if (itemCustomer4 != null) itemCustomer4.setOnClickListener(customerClickListener);

        if (btnAddCustomer != null) {
            btnAddCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomerDialog(true); // Add new customer
                }
            });
        }

        View btnNoti = findViewById(R.id.btnNoti);
        if (btnNoti != null) {
            btnNoti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CustomerManager.this, ManagerProfile.class));
                }
            });
        }
    }

    private boolean isAccountLocked = false; // Mock data for lock state

    private void showCustomerDialog(boolean isNewCustomer) {
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
        } else {
            tvTitle.setText("Thông tin khách hàng");
            btnLock.setVisibility(View.VISIBLE);
            
            // Setup text color for unlock/lock button based on mock state
            updateLockButtonUI(btnLock);

            // Dummy data for edit
            etName.setText("Nguyễn Hoàng");
            etPhone.setText("0901234567");
            etEmail.setText("hoang.nguyen@email.com");
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CustomerManager.this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAccountLocked = !isAccountLocked;
                
                if (isAccountLocked) {
                    Toast.makeText(CustomerManager.this, "Tài khoản đã bị khóa", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CustomerManager.this, "Tài khoản đã được mở khóa", Toast.LENGTH_SHORT).show();
                }
                
                updateLockButtonUI(btnLock);
                // We keep the dialog open after lock/unlock to allow user to confirm status
            }
        });

        dialog.show();
    }
    
    private void updateLockButtonUI(Button btnLock) {
        if (isAccountLocked) {
            btnLock.setText("Mở khóa tài khoản");
            btnLock.setBackgroundColor(Color.parseColor("#059669")); // Green color
        } else {
            btnLock.setText("Khóa tài khoản");
            btnLock.setBackgroundColor(Color.parseColor("#EF4444")); // Red color
        }
    }
}