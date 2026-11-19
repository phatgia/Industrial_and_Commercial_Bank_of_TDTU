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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.adapter.TransactionAdapter;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionItem;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionsData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryFragment extends Fragment {

    private ImageButton imgProfile, btnBack;
    private TextView txtName, tvMoneyIn, tvMoneyOut;
    private EditText etSearchTx;
    private Button btnFilterAll, btnFilterOut, btnFilterIn;
    private ListView lvTransactions;

    private SessionManager sessionManager;
    private final List<TransactionItem> allItems = new ArrayList<>();
    private final List<TransactionItem> filteredItems = new ArrayList<>();
    private TransactionAdapter adapter;
    private String currentFilter = "ALL"; // ALL | IN | OUT

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_history, container, false);

        sessionManager = new SessionManager(requireContext());
        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;

        txtName = view.findViewById(R.id.txtName);
        tvMoneyIn = view.findViewById(R.id.tvMoneyIn);
        tvMoneyOut = view.findViewById(R.id.tvMoneyOut);
        etSearchTx = view.findViewById(R.id.etSearchTx);
        btnFilterAll = view.findViewById(R.id.btnFilterAll);
        btnFilterOut = view.findViewById(R.id.btnFilterOut);
        btnFilterIn = view.findViewById(R.id.btnFilterIn);
        lvTransactions = view.findViewById(R.id.lvTransactions);

        if (!sessionManager.getFullName().isEmpty()) {
            txtName.setText(sessionManager.getFullName());
        }

        adapter = new TransactionAdapter(requireContext(), filteredItems);
        lvTransactions.setAdapter(adapter);

        imgProfile = view.findViewById(R.id.imgProfile);
        imgProfile.setOnClickListener(v -> {
            if (mainActivity != null) {
                mainActivity.loadFragment(new ProfileFragment(), true);
                mainActivity.updateNavUI(1);
            }
        });

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (btnFilterAll != null) btnFilterAll.setOnClickListener(v -> { currentFilter = "ALL"; applyFilter(); });
        if (btnFilterOut != null) btnFilterOut.setOnClickListener(v -> { currentFilter = "OUT"; applyFilter(); });
        if (btnFilterIn != null) btnFilterIn.setOnClickListener(v -> { currentFilter = "IN"; applyFilter(); });

        if (etSearchTx != null) {
            etSearchTx.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilter();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        loadHistory();

        return view;
    }

    private void loadHistory() {
        if (!sessionManager.isLoggedIn()) return;

        ApiClient.getService().getTransactionHistory(50).enqueue(new Callback<ApiResponse<TransactionsData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<TransactionsData>> call, @NonNull Response<ApiResponse<TransactionsData>> response) {
                if (!isAdded()) return;
                ApiResponse<TransactionsData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    allItems.clear();
                    if (body.getData().getTransactions() != null) {
                        allItems.addAll(body.getData().getTransactions());
                    }
                    updateSummaryTotals();
                    applyFilter();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<TransactionsData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi tải lịch sử giao dịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSummaryTotals() {
        double totalIn = 0;
        double totalOut = 0;
        for (TransactionItem item : allItems) {
            if ("DEPOSIT".equalsIgnoreCase(item.getTxType())) {
                totalIn += item.getAmount();
            } else {
                totalOut += item.getAmount();
            }
        }
        NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
        if (tvMoneyIn != null) tvMoneyIn.setText("+" + vnd.format(Math.round(totalIn)) + " đ");
        if (tvMoneyOut != null) tvMoneyOut.setText("-" + vnd.format(Math.round(totalOut)) + " đ");
    }

    private void applyFilter() {
        String query = etSearchTx != null
                ? etSearchTx.getText().toString().trim().toLowerCase(Locale.ROOT)
                : "";
        filteredItems.clear();

        for (TransactionItem item : allItems) {
            boolean isIncoming = "DEPOSIT".equalsIgnoreCase(item.getTxType());
            if ("IN".equals(currentFilter) && !isIncoming) continue;
            if ("OUT".equals(currentFilter) && isIncoming) continue;

            if (!query.isEmpty()) {
                String desc = item.getDescription() != null ? item.getDescription().toLowerCase(Locale.ROOT) : "";
                String bank = item.getToBankName() != null ? item.getToBankName().toLowerCase(Locale.ROOT) : "";
                if (!desc.contains(query) && !bank.contains(query)) continue;
            }
            filteredItems.add(item);
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
