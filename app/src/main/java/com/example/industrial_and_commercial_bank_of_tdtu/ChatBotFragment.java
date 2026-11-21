package com.example.industrial_and_commercial_bank_of_tdtu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ChatData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ChatRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatBotFragment extends Fragment {

    private LinearLayout llChatMessages;
    private ScrollView scrollChat;
    private EditText etChatInput;
    private ImageButton btnSendChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_chat_bot, container, false);

        MainActivity mainActivity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        SessionManager sessionManager = new SessionManager(requireContext());

        TextView txtName = view.findViewById(R.id.txtName);
        if (txtName != null && !sessionManager.getFullName().isEmpty()) {
            txtName.setText(sessionManager.getFullName());
        }

        llChatMessages = view.findViewById(R.id.llChatMessages);
        scrollChat = view.findViewById(R.id.scrollChat);
        etChatInput = view.findViewById(R.id.etChatInput);
        btnSendChat = view.findViewById(R.id.btnSendChat);

        ImageButton imgProfile = view.findViewById(R.id.imgProfile);
        if (imgProfile != null) {
            imgProfile.setOnClickListener(v -> {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new ProfileFragment(), true);
                    mainActivity.updateNavUI(1);
                }
            });
        }

        View cardMapAction = view.findViewById(R.id.cardMapAction);
        if (cardMapAction != null) {
            cardMapAction.setOnClickListener(v -> {
                if (mainActivity != null) {
                    mainActivity.loadFragment(new BranchMapFragment(), true);
                }
            });
        }

        addBotMessage("Xin chào " + (sessionManager.getFullName().isEmpty() ? "bạn" : sessionManager.getFullName())
                + "! Tôi là Nova AI. Bạn có thể hỏi về số dư, nạp tiền điện thoại, thanh toán điện nước hoặc tìm chi nhánh gần nhất.", "NONE", mainActivity);

        if (btnSendChat != null) {
            btnSendChat.setOnClickListener(v -> sendQuestion(mainActivity));
        }

        return view;
    }

    private void sendQuestion(MainActivity mainActivity) {
        String text = etChatInput.getText().toString().trim();
        if (text.isEmpty()) return;

        etChatInput.setText("");
        addUserMessage(text);

        btnSendChat.setEnabled(false);
        ApiClient.getService().askChatbot(new ChatRequest(text)).enqueue(new Callback<ApiResponse<ChatData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<ChatData>> call,
                                   @NonNull Response<ApiResponse<ChatData>> response) {
                if (!isAdded()) return;
                btnSendChat.setEnabled(true);

                ApiResponse<ChatData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    addBotMessage(body.getData().getReply(), body.getData().getAction(), mainActivity);
                } else {
                    String fallbackMsg = "Không nhận được phản hồi từ Nova AI (HTTP " + response.code() + ").";
                    String msg = ApiErrorHelper.extractMessage(response, fallbackMsg);
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<ChatData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnSendChat.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addUserMessage(String message) {
        TextView tv = new TextView(requireContext());
        tv.setText(message);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundResource(R.drawable.bg_btn);
        tv.setPadding(28, 18, 28, 18);
        tv.setTextSize(14f);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.END;
        lp.topMargin = 12;
        lp.leftMargin = 64;
        tv.setLayoutParams(lp);
        llChatMessages.addView(tv);
        scrollToBottom();
    }

    private void addBotMessage(String message, String action, MainActivity mainActivity) {
        LinearLayout bubble = new LinearLayout(requireContext());
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_card_white);
        bubble.setPadding(28, 20, 28, 20);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.gravity = Gravity.START;
        lp.topMargin = 12;
        lp.rightMargin = 48;
        bubble.setLayoutParams(lp);

        TextView tv = new TextView(requireContext());
        tv.setText(message);
        tv.setTextColor(Color.parseColor("#1E293B"));
        tv.setTextSize(14f);
        bubble.addView(tv);

        Button actionBtn = createActionButton(action, mainActivity);
        if (actionBtn != null) {
            bubble.addView(actionBtn);
        }

        llChatMessages.addView(bubble);
        scrollToBottom();
    }

    @Nullable
    private Button createActionButton(String action, MainActivity mainActivity) {
        if (action == null || "NONE".equalsIgnoreCase(action) || mainActivity == null) return null;

        String label;
        Fragment target;
        int navTab = -1;

        if ("PAY_PHONE".equalsIgnoreCase(action)) {
            label = "Mở Nạp tiền điện thoại";
            target = new TopUpFragment();
            navTab = 2;
        } else if ("PAY_BILL".equalsIgnoreCase(action)) {
            label = "Mở Thanh toán hóa đơn";
            target = new UtilitiesFragment();
            navTab = 2;
        } else if ("VIEW_TRANSACTIONS".equalsIgnoreCase(action)) {
            label = "Xem Lịch sử giao dịch";
            target = new TransactionHistoryFragment();
        } else if ("FIND_BRANCH".equalsIgnoreCase(action)) {
            label = "Xem Bản đồ Chi nhánh";
            target = new BranchMapFragment();
        } else {
            return null;
        }

        Button btn = new Button(requireContext());
        btn.setText(label);
        btn.setTextSize(12f);
        btn.setAllCaps(false);
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.parseColor("#0B63E5"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.topMargin = 14;
        btn.setLayoutParams(lp);

        final int tab = navTab;
        btn.setOnClickListener(v -> {
            mainActivity.loadFragment(target, true);
            if (tab >= 0) mainActivity.updateNavUI(tab);
        });
        return btn;
    }

    private void scrollToBottom() {
        if (scrollChat != null) {
            scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
        }
    }
}
