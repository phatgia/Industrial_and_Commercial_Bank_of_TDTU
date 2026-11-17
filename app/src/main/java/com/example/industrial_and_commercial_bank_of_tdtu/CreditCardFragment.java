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
import androidx.fragment.app.Fragment;

public class CreditCardFragment extends Fragment {

    ImageButton imgProfile, btnBack;
    Button btnDeposit, btnWithdraw, btnTransfer, btnAddCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_credit_card, container, false);

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

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        btnDeposit = view.findViewById(R.id.btnDeposit);
        btnWithdraw = view.findViewById(R.id.btnWithdraw);
        btnTransfer = view.findViewById(R.id.btnTransfer);
        btnAddCard = view.findViewById(R.id.btnAddCard);

        if (btnDeposit != null) btnDeposit.setOnClickListener(v -> showDepositDialog());
        if (btnWithdraw != null) btnWithdraw.setOnClickListener(v -> showWithdrawDialog());
        if (btnTransfer != null) btnTransfer.setOnClickListener(v -> showTransferDialog());
        if (btnAddCard != null) btnAddCard.setOnClickListener(v -> showActionDialog("Thêm thẻ mới", "Nhập số thẻ", "Nhập tên chủ thẻ"));

        return view;
    }

    private void showWithdrawDialog() {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_withdraw);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Yêu cầu rút tiền đang xử lý", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showTransferDialog() {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_transfer);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Yêu cầu chuyển tiền đang xử lý", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDepositDialog() {
        if (getContext() == null) return;
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_deposit);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Yêu cầu nạp tiền đang xử lý", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showActionDialog(String title, String hint1) {
        showActionDialog(title, hint1, null);
    }

    private void showActionDialog(String title, String hint1, String hint2) {
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