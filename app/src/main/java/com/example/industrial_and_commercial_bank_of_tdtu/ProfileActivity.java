package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout btnCreditCard, btnTransHis, navUtility, navAI, navHome, navProfile;
    ConstraintLayout cardBalance;
    ImageButton imgProfile, btnCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnCreditCard = findViewById(R.id.creditcard1);
        btnCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        btnTransHis = findViewById(R.id.btnTransHis);
        btnTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(ProfileActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        cardBalance = findViewById(R.id.cardBalance);
        cardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CreditCardActivity.class);
                startActivity(intent);
            }
        });

        navUtility = findViewById(R.id.navUtilities);
        navUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_utilities = new Intent(ProfileActivity.this, UtilitiesActivity.class);
                startActivity(chuyen_man_hinh_utilities);
            }
        });

        navAI = findViewById(R.id.navAI);
        navAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_profile = new Intent(ProfileActivity.this, ChatBotActivity.class);
                startActivity(chuyen_man_hinh_profile);
            }
        });

        navHome = findViewById(R.id.navHome);
        navHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        navProfile = findViewById(R.id.navAccount);
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnCenter = findViewById(R.id.btnCenter);
        btnCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}