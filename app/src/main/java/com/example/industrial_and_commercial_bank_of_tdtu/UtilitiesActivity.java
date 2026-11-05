package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UtilitiesActivity extends AppCompatActivity {

    LinearLayout navProfile, navAI, navHome;
    ImageButton imgProfile, btnCenter;
    ConstraintLayout locationCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utilities);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(UtilitiesActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        locationCard = findViewById(R.id.location);
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_chat = new Intent(UtilitiesActivity.this, ChatBotActivity.class);
                startActivity(chuyen_man_hinh_chat);
            }
        });

        navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UtilitiesActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        navProfile = findViewById(R.id.navAccount);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(UtilitiesActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        navAI = findViewById(R.id.navAI);
        navAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_chat = new Intent(UtilitiesActivity.this, ChatBotActivity.class);
                startActivity(chuyen_man_hinh_chat);
            }
        });

        btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UtilitiesActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}