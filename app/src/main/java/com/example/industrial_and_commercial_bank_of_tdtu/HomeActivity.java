package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnUtility, btnMap, imgProfile, imgNext, btnTranferOut, btnTranferIn, btnCenter;
    LinearLayout navUtility, navProfile, navAI;
    TextView txtTransHis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chuyen_man_hinh_profile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        imgNext = findViewById(R.id.imgNext);
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        btnTranferOut = findViewById(R.id.btnTranferOut);
        btnTranferOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        btnTranferIn = findViewById(R.id.btnTranferIn);
        btnTranferIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        btnUtility = findViewById(R.id.btnBill);
        btnUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(HomeActivity.this, UtilitiesActivity.class);
                startActivity(chuyen_man_hinh_utilities);
            }
        });

        navUtility = findViewById(R.id.navUtilities);
        navUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(HomeActivity.this, UtilitiesActivity.class);
                startActivity(chuyen_man_hinh_utilities);
            }
        });

        navProfile = findViewById(R.id.navAccount);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        navAI = findViewById(R.id.navAI);
        navAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

        txtTransHis = findViewById(R.id.txtTransHis);
        txtTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chuyen_man_hinh_profile = new Intent(HomeActivity.this, TransactionHistoryActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        btnMap = findViewById(R.id.btnLocation);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

        btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}