package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    Button btnLogin;
    public static int CODE_STATUS_HOME =1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chuyen_man_hinh_home = new Intent(MainActivity.this, HomeActivity.class);
                startActivityForResult(chuyen_man_hinh_home, CODE_STATUS_HOME);
            }
        });
    }
}