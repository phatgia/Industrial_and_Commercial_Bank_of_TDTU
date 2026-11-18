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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.adapter.TransactionAdapter;
import com.example.industrial_and_commercial_bank_of_tdtu.model.AccountsData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BankAccount;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionItem;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionsData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ImageButton btnUtility, btnMap, imgProfile, imgNext, btnTranferOut, btnTranferIn, imgSee;
    private TextView txtTransHis, txtName, txtBalance;
    private ListView lvTransactions;

    private SessionManager sessionManager;
    private boolean isBalanceVisible = true;
    private String formattedBalance = "0 đ";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);

        sessionManager = new SessionManager(requireContext());
        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;

        txtName = view.findViewById(R.id.txtName);
        txtBalance = view.findViewById(R.id.txtBalance);
        imgSee = view.findViewById(R.id.imgSee);
        lvTransactions = view.findViewById(R.id.lv);

        if (!sessionManager.getFullName().isEmpty()) {
            txtName.setText(sessionManager.getFullName());
        }

        imgSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBalanceVisible = !isBalanceVisible;
                txtBalance.setText(isBalanceVisible ? formattedBalance : "•••••••• đ");
            }
        });

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
        btnTranferOut.setOnClickListener(v -> showTransferDialog());

        btnTranferIn = view.findViewById(R.id.btnTranferIn);
        btnTranferIn.setOnClickListener(v -> showDepositDialog());

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

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        if (!sessionManager.isLoggedIn()) return;

        ApiClient.getService().getAccounts().enqueue(new Callback<ApiResponse<AccountsData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AccountsData>> call, @NonNull Response<ApiResponse<AccountsData>> response) {
                if (!isAdded()) return;
                ApiResponse<AccountsData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    List<BankAccount> accounts = body.getData().getAccounts();
                    double checkingOrTotal = 0;
                    if (accounts != null) {
                        for (BankAccount acc : accounts) {
                            if ("CHECKING".equalsIgnoreCase(acc.getAccountType())) {
                                checkingOrTotal = acc.getBalance();
                                break;
                            }
                        }
                    }
                    NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
                    formattedBalance = vnd.format(Math.round(checkingOrTotal)) + " đ";
                    txtBalance.setText(isBalanceVisible ? formattedBalance : "•••••••• đ");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AccountsData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không tải được số dư: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ApiClient.getService().getTransactionHistory(10).enqueue(new Callback<ApiResponse<TransactionsData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<TransactionsData>> call, @NonNull Response<ApiResponse<TransactionsData>> response) {
                if (!isAdded()) return;
                ApiResponse<TransactionsData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    List<TransactionItem> items = body.getData().getTransactions();
                    if (items != null) {
                        lvTransactions.setAdapter(new TransactionAdapter(requireContext(), items));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<TransactionsData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Không tải được lịch sử giao dịch.", Toast.LENGTH_SHORT).show();
            }
        });
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
}