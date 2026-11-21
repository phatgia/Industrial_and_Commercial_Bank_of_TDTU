package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import java.util.Locale;

public class ManagerProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile);

        SessionManager session = new SessionManager(this);
        TextView tvOfficerName = findViewById(R.id.tvOfficerName);
        TextView tvOfficerId = findViewById(R.id.tvOfficerId);
        TextView tvOfficerEmail = findViewById(R.id.tvOfficerEmail);
        if (tvOfficerName != null && !session.getFullName().isEmpty()) {
            tvOfficerName.setText(session.getFullName());
        }
        if (tvOfficerId != null && session.getUserId() > 0) {
            tvOfficerId.setText(String.format(Locale.US, "MGR-%05d", session.getUserId()));
        }
        if (tvOfficerEmail != null && !session.getEmail().isEmpty()) {
            tvOfficerEmail.setText(session.getEmail());
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SessionManager().logoutAndClear();
                    Intent intent = new Intent(ManagerProfile.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}