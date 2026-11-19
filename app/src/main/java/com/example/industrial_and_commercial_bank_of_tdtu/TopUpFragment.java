package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BalanceUpdateData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.DepositWithdrawRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionResult;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpFragment extends Fragment {

    private EditText etPhone;
    private Button btnContinue;
    private TextView tvFinalAmount, tvDiscount;
    private TextView[] valButtons;
    private double selectedValue = 0;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topup, container, false);

        sessionManager = new SessionManager(requireContext());
        etPhone = view.findViewById(R.id.etPhone);
        btnContinue = view.findViewById(R.id.btnContinue);
        tvFinalAmount = view.findViewById(R.id.tvFinalAmount);
        tvDiscount = view.findViewById(R.id.tvDiscount);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        valButtons = new TextView[] {
            view.findViewById(R.id.val10),
            view.findViewById(R.id.val20),
            view.findViewById(R.id.val50),
            view.findViewById(R.id.val100),
            view.findViewById(R.id.val200),
            view.findViewById(R.id.val500)
        };

        double[] values = {10000, 20000, 50000, 100000, 200000, 500000};

        for (int i = 0; i < valButtons.length; i++) {
            final int index = i;
            valButtons[i].setOnClickListener(v -> selectValue(valButtons[index], values[index]));
        }

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEnableButton();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setOnClickListener(v -> processPayment());

        return view;
    }

    private void selectValue(TextView selectedBtn, double value) {
        for (TextView btn : valButtons) {
            btn.setBackgroundResource(R.drawable.bg_card_white);
        }

        selectedBtn.setBackgroundResource(R.drawable.bg_input_stroke);

        selectedValue = value;
        double finalAmount = value * 0.98; // 2% discount

        NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvFinalAmount.setText(vnd.format(finalAmount) + " VND");
        tvDiscount.setVisibility(View.VISIBLE);

        checkEnableButton();
    }

    private void checkEnableButton() {
        String phone = etPhone.getText().toString().trim();
        btnContinue.setEnabled(!phone.isEmpty() && selectedValue > 0);
    }

    private void processPayment() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty() || selectedValue <= 0) {
            Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại và chọn mệnh giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalAmount = selectedValue * 0.98;
        String desc = "Nạp tiền điện thoại " + phone;

        btnContinue.setEnabled(false);
        DepositWithdrawRequest req = new DepositWithdrawRequest("WITHDRAW", finalAmount, desc);
        ApiClient.getService().depositOrWithdraw(req).enqueue(new Callback<ApiResponse<BalanceUpdateData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BalanceUpdateData>> call,
                                   @NonNull Response<ApiResponse<BalanceUpdateData>> response) {
                if (!isAdded()) return;
                btnContinue.setEnabled(true);

                ApiResponse<BalanceUpdateData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    String transactionId = "FT" + body.getData().getTxId();
                    String sourceAccount = !sessionManager.getCheckingAccountNumber().isEmpty()
                            ? sessionManager.getCheckingAccountNumber()
                            : "Tài khoản thanh toán";

                    TransactionResult result = new TransactionResult(
                            transactionId,
                            currentDate,
                            finalAmount,
                            "Nạp tiền điện thoại - " + phone,
                            "Nạp thẻ điện thoại",
                            "Thành công",
                            sourceAccount
                    );

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadFragment(ReceiptFragment.newInstance(result), true);
                    }
                } else {
                    String msg = ApiErrorHelper.extractMessage(response, "Nạp tiền điện thoại thất bại.");
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<BalanceUpdateData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnContinue.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
