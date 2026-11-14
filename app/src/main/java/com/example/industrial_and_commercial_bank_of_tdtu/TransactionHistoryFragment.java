package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryFragment extends Fragment {

    private ImageButton imgProfile, btnBack;
    private TextView txtName;
    private ListView lvTransactions;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_history, container, false);

        sessionManager = new SessionManager(requireContext());
        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;

        txtName = view.findViewById(R.id.txtName);
        if (!sessionManager.getFullName().isEmpty()) {
            txtName.setText(sessionManager.getFullName());
        }

        lvTransactions = view.findViewById(R.id.lvTransactions);

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

        loadHistory();

        return view;
    }

    private void loadHistory() {
        int userId = sessionManager.getUserId();
        if (userId <= 0) return;

        ApiClient.getService().getTransactionHistory(userId, 50).enqueue(new Callback<ApiResponse<TransactionsData>>() {
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
                Toast.makeText(requireContext(), "Lỗi tải lịch sử giao dịch: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
