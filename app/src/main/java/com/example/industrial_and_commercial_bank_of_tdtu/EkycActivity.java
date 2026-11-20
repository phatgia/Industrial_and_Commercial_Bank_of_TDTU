package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class EkycActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekyc);

        ImageButton btnBackEkyc = findViewById(R.id.btnBackEkyc);
        btnBackEkyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View btnCaptureFront = findViewById(R.id.btnCaptureFront);
        btnCaptureFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EkycActivity.this, "Mở camera chụp mặt trước...", Toast.LENGTH_SHORT).show();
            }
        });

        View btnCaptureBack = findViewById(R.id.btnCaptureBack);
        btnCaptureBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EkycActivity.this, "Mở camera chụp mặt sau...", Toast.LENGTH_SHORT).show();
            }
        });

        View btnScanFace = findViewById(R.id.btnScanFace);
        btnScanFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EkycActivity.this, "Mở Face ID...", Toast.LENGTH_SHORT).show();
            }
        });

        MaterialButton btnSubmitEkyc = findViewById(R.id.btnSubmitEkyc);
        btnSubmitEkyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EkycActivity.this, "eKYC hoàn tất. Dữ liệu đang được xác thực.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}