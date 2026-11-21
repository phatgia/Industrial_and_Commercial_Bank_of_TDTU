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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BranchMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private final double branchLat = 10.732669;
    private final double branchLng = 106.699770;
    private final String branchName = "ICBTDTU - Chi nhánh Quận 7";
    private final String branchAddress = "19 Nguyễn Hữu Thọ, P. Tân Phong, Quận 7, TP.HCM";

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ImageButton btnDirections = view.findViewById(R.id.btnDirections);
        if (btnDirections != null) {
            btnDirections.setOnClickListener(v -> {
                String geoUri = "https://maps.google.com/maps?daddr=" + branchLat + "," + branchLng;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                if (getActivity() != null && intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy ứng dụng Google Maps", Toast.LENGTH_SHORT).show();
                }
            });
        }

        View fabMyLocation = view.findViewById(R.id.fabMyLocation);
        if (fabMyLocation != null) {
            fabMyLocation.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Đang lấy vị trí hiện tại...", Toast.LENGTH_SHORT).show();
                if (mMap != null) {
                    LatLng loc = new LatLng(branchLat, branchLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17f));
                }
            });
        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker and move the camera
        LatLng branchLocation = new LatLng(branchLat, branchLng);
        mMap.addMarker(new MarkerOptions()
                .position(branchLocation)
                .title(branchName)
                .snippet(branchAddress));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(branchLocation, 15f));
    }
}