package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SystemConfig extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_config);

        Button btnSaveConfig = findViewById(R.id.btnSaveConfig);
        if (btnSaveConfig != null) {
            btnSaveConfig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SystemConfig.this, "Cập nhật lãi suất thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}