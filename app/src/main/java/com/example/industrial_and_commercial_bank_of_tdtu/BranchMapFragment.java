package com.example.industrial_and_commercial_bank_of_tdtu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BranchMapFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_branch_map, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackMap);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        ImageButton btnDirections = view.findViewById(R.id.btnDirections);
        if (btnDirections != null) {
            btnDirections.setOnClickListener(v -> {
                // Mock coordinate for TDTU
                String geoUri = "http://maps.google.com/maps?daddr=10.7326689,106.699762";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                if (getActivity() != null && intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy ứng dụng Google Maps", Toast.LENGTH_SHORT).show();
                }
            });
        }

        view.findViewById(R.id.fabMyLocation).setOnClickListener(v -> {
             Toast.makeText(getContext(), "Đang lấy vị trí hiện tại...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}