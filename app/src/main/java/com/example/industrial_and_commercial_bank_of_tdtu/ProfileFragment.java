package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

public class ProfileFragment extends Fragment {

    private LinearLayout btnCreditCard, btnTransHis;
    private ConstraintLayout cardBalance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        SessionManager sessionManager = new SessionManager(requireContext());

        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtCardName = view.findViewById(R.id.txtBalance);
        TextView txtPhone = view.findViewById(R.id.txtProfilePhone);
        TextView txtEmail = view.findViewById(R.id.txtProfileEmail);
        TextView txtEkyc = view.findViewById(R.id.txtProfileEkyc);
        TextView txtBranch = view.findViewById(R.id.txtProfileBranch);

        String fullName = sessionManager.getFullName();
        if (!fullName.isEmpty()) {
            if (txtName != null) txtName.setText(fullName);
            if (txtCardName != null) txtCardName.setText(fullName);
        }
        if (txtPhone != null && !sessionManager.getPhone().isEmpty()) {
            txtPhone.setText(sessionManager.getPhone());
        }
        if (txtEmail != null && !sessionManager.getEmail().isEmpty()) {
            txtEmail.setText(sessionManager.getEmail());
        }
        if (txtEkyc != null) {
            txtEkyc.setText(sessionManager.isEkycVerified() ? "Đã xác thực eKYC" : "Chưa xác thực eKYC");
        }
        if (txtBranch != null) {
            txtBranch.setText("ICBT Tôn Đức Thắng");
        }

        btnCreditCard = view.findViewById(R.id.creditcard1);
        btnCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new CreditCardFragment(), true);
                }
            }
        });

        btnTransHis = view.findViewById(R.id.btnTransHis);
        btnTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new TransactionHistoryFragment(), true);
                }
            }
        });

        cardBalance = view.findViewById(R.id.cardBalance);
        cardBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new CreditCardFragment(), true);
                }
            }
        });

        return view;
    }
}
