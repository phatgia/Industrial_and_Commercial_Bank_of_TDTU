package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionResult;
import java.text.NumberFormat;
import java.util.Locale;

public class ReceiptFragment extends Fragment {

    private TransactionResult result;

    public static ReceiptFragment newInstance(TransactionResult result) {
        ReceiptFragment fragment = new ReceiptFragment();
        Bundle args = new Bundle();
        args.putSerializable("RESULT", result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            result = (TransactionResult) getArguments().getSerializable("RESULT");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);

        if (result != null) {
            TextView tvAmount = view.findViewById(R.id.tvAmount);
            TextView tvTransId = view.findViewById(R.id.tvTransId);
            TextView tvTransDate = view.findViewById(R.id.tvTransDate);
            TextView tvTransService = view.findViewById(R.id.tvTransService);
            TextView tvTransProvider = view.findViewById(R.id.tvTransProvider);
            TextView tvTransSource = view.findViewById(R.id.tvTransSource);

            NumberFormat vnd = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvAmount.setText(vnd.format(result.getAmount()) + " VND");

            tvTransId.setText(result.getTransactionId());
            tvTransDate.setText(result.getDate());
            tvTransService.setText(result.getServiceName());
            tvTransProvider.setText(result.getProvider());
            tvTransSource.setText(result.getAccountSource());
        }

        Button btnDone = view.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                // Clear back stack and return to Utilities or Home
                getActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                ((MainActivity) getActivity()).loadFragment(new UtilitiesFragment(), false);
                ((MainActivity) getActivity()).updateNavUI(2);
            }
        });

        return view;
    }
}