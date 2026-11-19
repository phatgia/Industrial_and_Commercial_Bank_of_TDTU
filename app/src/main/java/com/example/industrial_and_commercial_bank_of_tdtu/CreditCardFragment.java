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

import com.example.industrial_and_commercial_bank_of_tdtu.model.AccountsData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BankAccount;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;
import com.example.industrial_and_commercial_bank_of_tdtu.ui.TransactionDialogHelper;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardFragment extends Fragment {

    private ImageButton imgProfile, btnBack;
    private Button btnDeposit, btnWithdraw, btnTransfer, btnAddCard;
    private TextView txtName, txtCardHolderName;
    private TextView txtCheckingNumber, txtCheckingBalance;
    private TextView txtSavingNumber, txtSavingBalance, txtSavingProfit;
    private TextView txtMortgageNumber, txtMortgageBalance, txtMortgagePayment;

    private SessionManager sessionManager;
    private String formattedCheckingBalance = "0 đ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_credit_card, container, false);

        sessionManager = new SessionManager(requireContext());
        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;

        txtName = view.findViewById(R.id.txtName);
        txtCardHolderName = view.findViewById(R.id.txtCardHolderName);
        txtCheckingNumber = view.findViewById(R.id.txtCheckingNumber);
        txtCheckingBalance = view.findViewById(R.id.txtCheckingBalance);
        txtSavingNumber = view.findViewById(R.id.txtSavingNumber);
        txtSavingBalance = view.findViewById(R.id.txtSavingBalance);
        txtSavingProfit = view.findViewById(R.id.txtSavingProfit);
        txtMortgageNumber = view.findViewById(R.id.txtMortgageNumber);
        txtMortgageBalance = view.findViewById(R.id.txtMortgageBalance);
        txtMortgagePayment = view.findViewById(R.id.txtMortgagePayment);

        String fullName = sessionManager.getFullName();
        if (!fullName.isEmpty()) {
            if (txtName != null) txtName.setText(fullName);
            if (txtCardHolderName != null) txtCardHolderName.setText(fullName.toUpperCase(Locale.ROOT));
        }

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

        if (btnDeposit != null) {
            btnDeposit.setOnClickListener(v -> TransactionDialogHelper.showDepositDialog(
                    requireContext(), formattedCheckingBalance, this::loadAccountsData));
        }
        if (btnWithdraw != null) {
            btnWithdraw.setOnClickListener(v -> TransactionDialogHelper.showWithdrawDialog(
                    requireContext(), formattedCheckingBalance, this::loadAccountsData));
        }
        if (btnTransfer != null) {
            btnTransfer.setOnClickListener(v -> TransactionDialogHelper.showTransferDialog(
                    requireContext(), formattedCheckingBalance, this::loadAccountsData));
        }
        if (btnAddCard != null) {
            btnAddCard.setOnClickListener(v -> showActionDialog("Thêm thẻ mới", "Nhập số thẻ", "Nhập tên chủ thẻ"));
        }

        loadAccountsData();

        return view;
    }

    private void loadAccountsData() {
        if (!sessionManager.isLoggedIn()) return;

        ApiClient.getService().getAccounts().enqueue(new Callback<ApiResponse<AccountsData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AccountsData>> call, @NonNull Response<ApiResponse<AccountsData>> response) {
                if (!isAdded()) return;
                ApiResponse<AccountsData> body = response.body();
                if (!response.isSuccessful() || body == null || !body.isSuccess() || body.getData() == null) {
                    return;
                }

                List<BankAccount> accounts = body.getData().getAccounts();
                if (accounts == null) return;

                NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
                for (BankAccount acc : accounts) {
                    String type = acc.getAccountType();
                    if ("CHECKING".equalsIgnoreCase(type)) {
                        formattedCheckingBalance = vnd.format(Math.round(acc.getBalance())) + " đ";
                        sessionManager.setCheckingAccountNumber(acc.getAccountNumber());
                        if (txtCheckingNumber != null) txtCheckingNumber.setText("STK: " + acc.getAccountNumber());
                        if (txtCheckingBalance != null) txtCheckingBalance.setText(formattedCheckingBalance);
                    } else if ("SAVING".equalsIgnoreCase(type)) {
                        if (txtSavingNumber != null) txtSavingNumber.setText("STK: " + acc.getAccountNumber());
                        if (txtSavingBalance != null) txtSavingBalance.setText(vnd.format(Math.round(acc.getBalance())) + " đ");
                        if (txtSavingProfit != null) {
                            txtSavingProfit.setText(String.format(Locale.US, "Lãi %.1f%%/năm • +%s đ/tháng",
                                    acc.getInterestRate(),
                                    vnd.format(Math.round(acc.getEstimatedMonthlyProfit()))));
                        }
                    } else if ("MORTGAGE".equalsIgnoreCase(type)) {
                        if (txtMortgageNumber != null) txtMortgageNumber.setText("HĐ: " + acc.getAccountNumber());
                        if (txtMortgageBalance != null) txtMortgageBalance.setText(vnd.format(Math.round(acc.getBalance())) + " đ");
                        if (txtMortgagePayment != null) {
                            String cycleText = "BIWEEKLY".equalsIgnoreCase(acc.getPaymentCycle()) ? "2 tuần" : "tháng";
                            txtMortgagePayment.setText("Trả " + vnd.format(Math.round(acc.getPeriodicPayment())) + " đ / " + cycleText);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AccountsData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không tải được danh sách tài khoản.", Toast.LENGTH_SHORT).show();
            }
        });
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
