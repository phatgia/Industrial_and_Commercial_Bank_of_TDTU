package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    ImageButton btnUtility, btnMap, imgProfile, imgNext, btnTranferOut, btnTranferIn;
    TextView txtTransHis;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;

        imgProfile = view.findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new ProfileFragment(), true);
                    mainActivity.updateNavUI(1);
                }
            }
        });

        imgNext = view.findViewById(R.id.imgNext);
        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new CreditCardFragment(), true);
                }
            }
        });

        btnTranferOut = view.findViewById(R.id.btnTranferOut);
        btnTranferOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new CreditCardFragment(), true);
                }
            }
        });

        btnTranferIn = view.findViewById(R.id.btnTranferIn);
        btnTranferIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new CreditCardFragment(), true);
                }
            }
        });

        btnUtility = view.findViewById(R.id.btnBill);
        btnUtility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new UtilitiesFragment(), true);
                    mainActivity.updateNavUI(2);
                }
            }
        });

        txtTransHis = view.findViewById(R.id.txtTransHis);
        txtTransHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new TransactionHistoryFragment(), true);
                }
            }
        });

        btnMap = view.findViewById(R.id.btnLocation);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new ChatBotFragment(), true);
                    mainActivity.updateNavUI(3);
                }
            }
        });

        return view;
    }
}