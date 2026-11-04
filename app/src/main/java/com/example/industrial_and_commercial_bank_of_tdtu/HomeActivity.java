package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnUtility;
    public static int CODE_STATUS_UTILITY=1000;
    public static int CODE_STATUS_PROFILE=1001;
    LinearLayout navUtility, navProfile;

    TextView txtTransHis;
    public static int CODE_STATUS_TRANS=1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnUtility = findViewById(R.id.btnBill);

        btnUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(HomeActivity.this, UtilitiesActivity.class);
                startActivityForResult(chuyen_man_hinh_utilities, CODE_STATUS_UTILITY);
            }
        });

        navUtility = findViewById(R.id.navUtilities);
        navUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(HomeActivity.this, UtilitiesActivity.class);
                startActivityForResult(chuyen_man_hinh_utilities, CODE_STATUS_UTILITY);
            }
        });

        navProfile = findViewById(R.id.navAccount);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivityForResult(chuyen_man_hinh_profile, CODE_STATUS_PROFILE);
            }
        });

        txtTransHis = findViewById(R.id.txtTransHis);
        txtTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chuyen_man_hinh_profile = new Intent(HomeActivity.this, TransactionHistoryActivity.class);
                startActivityForResult(chuyen_man_hinh_profile, CODE_STATUS_TRANS);
            }
        });

    }
}