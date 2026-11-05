package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ChatBotActivity extends AppCompatActivity {

    LinearLayout navUtility, navProfile, navHome, navAI;
    ImageButton imgProfile, btnCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(ChatBotActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        navUtility = findViewById(R.id.navUtilities);
        navUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(ChatBotActivity.this, UtilitiesActivity.class);
                startActivity(chuyen_man_hinh_utilities);
            }
        });

        navProfile = findViewById(R.id.navAccount);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(ChatBotActivity.this, ProfileActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatBotActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        navAI = findViewById(R.id.navAI);
        navAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatBotActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}