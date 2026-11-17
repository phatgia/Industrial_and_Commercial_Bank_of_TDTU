package com.example.industrial_and_commercial_bank_of_tdtu;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

public class UtilitiesFragment extends Fragment {

    ImageButton imgProfile;
    ConstraintLayout locationCard;
    ImageButton btnTranferOut, btnTranferIn, btnTranferOut1, btnTranferIn1, btnTranferOut2, btnTranferIn2, btnTranferOut3, btnTranferIn3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_utilities, container, false);

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

        locationCard = view.findViewById(R.id.location);
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new ChatBotFragment(), true);
                    mainActivity.updateNavUI(3);
                }
            }
        });

        // Initialize utility buttons
        btnTranferOut = view.findViewById(R.id.btnTranferOut); // Tiền điện
        btnTranferIn = view.findViewById(R.id.btnTranferIn); // Tiền nước
        btnTranferOut1 = view.findViewById(R.id.btnTranferOut1); // Nạp điện thoại
        btnTranferIn1 = view.findViewById(R.id.btnTranferIn1); // Vé máy bay
        btnTranferOut2 = view.findViewById(R.id.btnTranferOut2); // Vé xem phim
        btnTranferIn2 = view.findViewById(R.id.btnTranferIn2); // Đặt khách sạn
        btnTranferOut3 = view.findViewById(R.id.btnTranferOut3); // Mua sắm
        btnTranferIn3 = view.findViewById(R.id.btnTranferIn3); // Xem thêm

        if (btnTranferOut != null) btnTranferOut.setOnClickListener(v -> showUtilityDialog("Thanh toán tiền điện", "Nhập mã khách hàng EVN"));
        if (btnTranferIn != null) btnTranferIn.setOnClickListener(v -> showUtilityDialog("Thanh toán tiền nước", "Nhập mã khách hàng"));
        if (btnTranferOut1 != null) btnTranferOut1.setOnClickListener(v -> showUtilityDialog("Nạp tiền điện thoại", "Nhập số điện thoại", "Nhập số tiền cần nạp"));
        if (btnTranferIn1 != null) btnTranferIn1.setOnClickListener(v -> showUtilityDialog("Đặt vé máy bay", "Điểm đi - Điểm đến", "Ngày bay (DD/MM/YYYY)"));
        if (btnTranferOut2 != null) btnTranferOut2.setOnClickListener(v -> showUtilityDialog("Mua vé xem phim", "Nhập tên rạp chiếu", "Nhập tên phim"));
        if (btnTranferIn2 != null) btnTranferIn2.setOnClickListener(v -> showUtilityDialog("Đặt khách sạn", "Nhập thành phố / tên khách sạn"));
        if (btnTranferOut3 != null) btnTranferOut3.setOnClickListener(v -> showUtilityDialog("Mua sắm trực tuyến", "Tìm kiếm sản phẩm hoặc mã đơn"));
        if (btnTranferIn3 != null) btnTranferIn3.setOnClickListener(v -> Toast.makeText(getContext(), "Đang mở rộng các tiện ích...", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void showUtilityDialog(String title, String hint1) {
        showUtilityDialog(title, hint1, null);
    }

    private void showUtilityDialog(String title, String hint1, String hint2) {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_generic_form);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tvDialogTitle);
        EditText etInput1 = dialog.findViewById(R.id.etInput1);
        EditText etInput2 = dialog.findViewById(R.id.etInput2);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        tvTitle.setText(title);
        etInput1.setHint(hint1);

        if (hint2 != null) {
            etInput2.setVisibility(View.VISIBLE);
            etInput2.setHint(hint2);
        } else {
            etInput2.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(getContext(), title + " thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}