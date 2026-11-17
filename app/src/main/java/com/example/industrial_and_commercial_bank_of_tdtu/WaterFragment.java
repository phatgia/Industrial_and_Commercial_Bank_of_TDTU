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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionResult;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UtilityBill;
import com.example.industrial_and_commercial_bank_of_tdtu.repository.UtilityMockRepository;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterFragment extends Fragment {

    private EditText etCustomerId;
    private Button btnContinue;
    private LinearLayout llBillResult;
    private TextView tvBillCustomerName, tvBillPeriod, tvBillAmount;

    private UtilityBill currentBill;
    private boolean isBillFound = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_water_bill, container, false);

        etCustomerId = view.findViewById(R.id.etCustomerId);
        btnContinue = view.findViewById(R.id.btnContinue);
        llBillResult = view.findViewById(R.id.llBillResult);
        tvBillCustomerName = view.findViewById(R.id.tvBillCustomerName);
        tvBillPeriod = view.findViewById(R.id.tvBillPeriod);
        tvBillAmount = view.findViewById(R.id.tvBillAmount);

        ImageButton btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        etCustomerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isBillFound = false;
                llBillResult.setVisibility(View.GONE);
                btnContinue.setText("Tra cứu");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnContinue.setText("Tra cứu");
        btnContinue.setOnClickListener(v -> {
            if (!isBillFound) {
                searchBill();
            } else {
                processPayment();
            }
        });

        return view;
    }

    private void searchBill() {
        String customerId = etCustomerId.getText().toString().trim();
        if (customerId.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập mã danh bộ", Toast.LENGTH_SHORT).show();
            return;
        }

        currentBill = UtilityMockRepository.getInstance().getWaterBill(customerId);

        if (currentBill != null) {
            if (currentBill.isPaid()) {
                Toast.makeText(getContext(), "Hóa đơn này đã được thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            isBillFound = true;
            llBillResult.setVisibility(View.VISIBLE);
            tvBillCustomerName.setText(currentBill.getCustomerName());
            tvBillPeriod.setText(currentBill.getPeriod());

            NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvBillAmount.setText(vnd.format(currentBill.getAmount()) + " VND");

            btnContinue.setText("Thanh toán");
        } else {
            Toast.makeText(getContext(), "Không tìm thấy hóa đơn cho mã danh bộ này", Toast.LENGTH_SHORT).show();
            llBillResult.setVisibility(View.GONE);
        }
    }

    private void processPayment() {
        if (currentBill == null || getContext() == null) return;

        UtilityMockRepository.getInstance().markBillAsPaid("WATER", currentBill.getCustomerId());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        String transactionId = "FT" + System.currentTimeMillis();

        TransactionResult result = new TransactionResult(
                transactionId,
                currentDate,
                currentBill.getAmount(),
                "Thanh toán tiền nước",
                currentBill.getProvider(),
                "Thành công",
                "0098****2846"
        );

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).loadFragment(ReceiptFragment.newInstance(result), true);
        }
    }
}