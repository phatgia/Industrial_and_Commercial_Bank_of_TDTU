package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashBoard extends AppCompatActivity {

    TextView btnViewAllKYC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        btnViewAllKYC = findViewById(R.id.btnViewAllKYC);
        if (btnViewAllKYC != null) {
            btnViewAllKYC.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DashBoard.this, CustomerManager.class);
                    startActivity(intent);
                }
            });
        }
        
        View btnNoti = findViewById(R.id.btnNoti);
        if (btnNoti != null) {
            btnNoti.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashBoard.this, ManagerProfile.class));
                }
            });
        }

        View btnAdd = findViewById(R.id.btnAdd);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashBoard.this, SystemConfig.class));
                }
            });
        }
    }
}