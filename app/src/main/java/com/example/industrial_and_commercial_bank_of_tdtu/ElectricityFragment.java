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

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BalanceUpdateData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.PayBillRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionResult;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UtilityBillApiItem;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UtilityBillsData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElectricityFragment extends Fragment {

    private EditText etCustomerId;
    private Button btnContinue;
    private LinearLayout llBillResult;
    private TextView tvBillCustomerName, tvBillPeriod, tvBillAmount;

    private SessionManager sessionManager;
    private UtilityBillApiItem currentBill;
    private boolean isBillFound = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_electricity_bill, container, false);

        sessionManager = new SessionManager(requireContext());
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
                currentBill = null;
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
        String billCode = etCustomerId.getText().toString().trim().toUpperCase(Locale.ROOT);
        if (billCode.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập mã hóa đơn (VD: EVN-TDTU-1126)", Toast.LENGTH_SHORT).show();
            return;
        }

        btnContinue.setEnabled(false);
        ApiClient.getService().getUtilityBills("ELECTRICITY", billCode).enqueue(new Callback<ApiResponse<UtilityBillsData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UtilityBillsData>> call,
                                   @NonNull Response<ApiResponse<UtilityBillsData>> response) {
                if (!isAdded()) return;
                btnContinue.setEnabled(true);

                ApiResponse<UtilityBillsData> body = response.body();
                if (!response.isSuccessful() || body == null || !body.isSuccess() || body.getData() == null) {
                    String msg = ApiErrorHelper.extractMessage(response, "Không tra cứu được hóa đơn điện.");
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<UtilityBillApiItem> bills = body.getData().getBills();
                if (bills == null || bills.isEmpty()) {
                    Toast.makeText(requireContext(), "Không tìm thấy hóa đơn cho mã này", Toast.LENGTH_SHORT).show();
                    llBillResult.setVisibility(View.GONE);
                    return;
                }

                UtilityBillApiItem bill = bills.get(0);
                if (bill.isPaid()) {
                    Toast.makeText(requireContext(), "Hóa đơn này đã được thanh toán", Toast.LENGTH_SHORT).show();
                    llBillResult.setVisibility(View.GONE);
                    return;
                }

                currentBill = bill;
                isBillFound = true;
                llBillResult.setVisibility(View.VISIBLE);

                String customerName = !sessionManager.getFullName().isEmpty()
                        ? sessionManager.getFullName()
                        : bill.getProviderName();
                tvBillCustomerName.setText(customerName);
                tvBillPeriod.setText("Hạn: " + (bill.getDueDate() != null ? bill.getDueDate() : ""));

                NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
                tvBillAmount.setText(vnd.format(Math.round(bill.getAmount())) + " VND");
                btnContinue.setText("Thanh toán");
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UtilityBillsData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnContinue.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPayment() {
        if (currentBill == null) return;

        btnContinue.setEnabled(false);
        PayBillRequest req = new PayBillRequest(currentBill.getId(), currentBill.getBillCode());
        ApiClient.getService().payUtilityBill(req).enqueue(new Callback<ApiResponse<BalanceUpdateData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BalanceUpdateData>> call,
                                   @NonNull Response<ApiResponse<BalanceUpdateData>> response) {
                if (!isAdded()) return;
                btnContinue.setEnabled(true);

                ApiResponse<BalanceUpdateData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    sessionManager.invalidateDashboardCache();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    String transactionId = "FT" + body.getData().getTxId();
                    String sourceAccount = !sessionManager.getCheckingAccountNumber().isEmpty()
                            ? sessionManager.getCheckingAccountNumber()
                            : "Tài khoản thanh toán";

                    TransactionResult result = new TransactionResult(
                            transactionId,
                            currentDate,
                            currentBill.getAmount(),
                            "Thanh toán tiền điện",
                            currentBill.getProviderName(),
                            "Thành công",
                            sourceAccount
                    );

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadFragment(ReceiptFragment.newInstance(result), true);
                    }
                } else {
                    String msg = ApiErrorHelper.extractMessage(response, "Thanh toán thất bại.");
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
