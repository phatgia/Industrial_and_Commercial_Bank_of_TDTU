package com.example.industrial_and_commercial_bank_of_tdtu.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.industrial_and_commercial_bank_of_tdtu.R;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BalanceUpdateData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.DepositWithdrawRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.StripeCheckoutData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.StripeCheckoutRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferConfirmData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferOtpData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lop dung chung xu ly cac dialog Chuyen tien (co OTP 2FA), Nap tien va Rut tien qua API.
 */
public final class TransactionDialogHelper {

    private static final String STRIPE_OPTION = "Stripe (Thẻ quốc tế Test)";
    private static final String[] BANKS = {
            "ICBT Bank", "Vietcombank", "BIDV", "Techcombank", "MB Bank", "ACB"
    };
    private static final String[] DEPOSIT_SOURCES = {
            STRIPE_OPTION, "Vietcombank", "BIDV", "Techcombank", "MB Bank", "ACB"
    };

    private TransactionDialogHelper() {
    }

    public static void showTransferDialog(Context context, String currentBalanceText, Runnable onSuccess) {
        if (context == null) return;
        Dialog dialog = createStyledDialog(context, R.layout.dialog_transfer);

        TextView tvSourceBalance = dialog.findViewById(R.id.tvSourceBalance);
        LinearLayout llTargetBank = dialog.findViewById(R.id.llTargetBank);
        TextView tvTargetBankName = dialog.findViewById(R.id.tvTargetBankName);
        EditText etTargetAccount = dialog.findViewById(R.id.etTargetAccountNumber);
        EditText etMessage = dialog.findViewById(R.id.etTransferMessage);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        if (tvSourceBalance != null) {
            tvSourceBalance.setText(currentBalanceText);
        }
        if (llTargetBank != null && tvTargetBankName != null) {
            llTargetBank.setOnClickListener(v -> showBankPicker(context, tvTargetBankName));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String toAccount = etTargetAccount != null ? etTargetAccount.getText().toString().trim() : "";
            String toBank = tvTargetBankName != null ? tvTargetBankName.getText().toString().trim() : "ICBT Bank";
            String desc = etMessage != null ? etMessage.getText().toString().trim() : "";
            if (desc.isEmpty()) {
                desc = "Chuyển tiền";
            }
            double amount = parsePositiveAmount(etAmount);

            if (toAccount.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập số tài khoản nhận.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount <= 0) {
                Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ (> 0).", Toast.LENGTH_SHORT).show();
                return;
            }

            btnConfirm.setEnabled(false);
            TransferRequest req = TransferRequest.requestOtp(toAccount, toBank, amount, desc);
            ApiClient.getService().requestTransferOtp(req).enqueue(new Callback<ApiResponse<TransferOtpData>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<TransferOtpData>> call,
                                       @NonNull Response<ApiResponse<TransferOtpData>> response) {
                    btnConfirm.setEnabled(true);
                    ApiResponse<TransferOtpData> body = response.body();
                    if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                        showOtpConfirmDialog(context, dialog, body.getData(), onSuccess);
                    } else {
                        String msg = ApiErrorHelper.extractMessage(response, "Không thể tạo giao dịch chuyển tiền.");
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<TransferOtpData>> call, @NonNull Throwable t) {
                    btnConfirm.setEnabled(true);
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private static void showOtpConfirmDialog(Context context,
                                             Dialog parentDialog,
                                             TransferOtpData otpData,
                                             Runnable onSuccess) {
        EditText etOtp = new EditText(context);
        etOtp.setInputType(InputType.TYPE_CLASS_NUMBER);
        etOtp.setHint("Nhập mã OTP 6 số");
        etOtp.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        int pad = (int) (16 * context.getResources().getDisplayMetrics().density);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(pad, pad / 2, pad, 0);
        container.addView(etOtp);

        String message = "Mã OTP demo của bạn là: " + otpData.getOtpCode()
                + "\nVui lòng nhập mã OTP 6 số để hoàn tất giao dịch.";
        if (otpData.isRequiredEkyc()) {
            message += "\n(Giao dịch giá trị cao đã kiểm tra trạng thái eKYC)";
        }

        AlertDialog otpDialog = new AlertDialog.Builder(context)
                .setTitle("Xác thực OTP (2FA)")
                .setMessage(message)
                .setView(container)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xác nhận", null)
                .create();

        otpDialog.setOnShowListener(dialogInterface -> {
            Button positiveBtn = otpDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveBtn.setOnClickListener(v -> {
                String otpInput = etOtp.getText().toString().trim();
                if (otpInput.isEmpty()) {
                    Toast.makeText(context, "Vui lòng nhập mã OTP.", Toast.LENGTH_SHORT).show();
                    return;
                }

                positiveBtn.setEnabled(false);
                TransferRequest confirmReq = TransferRequest.confirm(otpData.getTxId(), otpInput);
                ApiClient.getService().confirmTransfer(confirmReq).enqueue(new Callback<ApiResponse<TransferConfirmData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<TransferConfirmData>> call,
                                           @NonNull Response<ApiResponse<TransferConfirmData>> response) {
                        positiveBtn.setEnabled(true);
                        ApiResponse<TransferConfirmData> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess()) {
                            otpDialog.dismiss();
                            parentDialog.dismiss();
                            Toast.makeText(context, "Chuyển tiền thành công!", Toast.LENGTH_SHORT).show();
                            if (onSuccess != null) {
                                onSuccess.run();
                            }
                        } else {
                            String msg = ApiErrorHelper.extractMessage(response, "Xác thực OTP thất bại.");
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<TransferConfirmData>> call, @NonNull Throwable t) {
                        positiveBtn.setEnabled(true);
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        otpDialog.show();
    }

    public static void showDepositDialog(Context context, String currentBalanceText, Runnable onSuccess) {
        if (context == null) return;
        Dialog dialog = createStyledDialog(context, R.layout.dialog_deposit);

        LinearLayout llSourceBank = dialog.findViewById(R.id.llSourceBank);
        TextView tvSourceBankName = dialog.findViewById(R.id.tvSourceBankName);
        EditText etSourceAccount = dialog.findViewById(R.id.etSourceAccountNumber);
        TextView tvTargetBalance = dialog.findViewById(R.id.tvTargetBalance);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        if (tvTargetBalance != null) {
            tvTargetBalance.setText(currentBalanceText);
        }
        if (tvSourceBankName != null) {
            tvSourceBankName.setText(STRIPE_OPTION);
        }
        if (llSourceBank != null && tvSourceBankName != null) {
            llSourceBank.setOnClickListener(v -> showBankPicker(context, tvSourceBankName, DEPOSIT_SOURCES));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String sourceBank = tvSourceBankName != null ? tvSourceBankName.getText().toString().trim() : STRIPE_OPTION;
            String sourceAcc = etSourceAccount != null ? etSourceAccount.getText().toString().trim() : "";
            double amount = parsePositiveAmount(etAmount);

            if (amount <= 0) {
                Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ (> 0).", Toast.LENGTH_SHORT).show();
                return;
            }

            if (STRIPE_OPTION.equals(sourceBank)) {
                startStripeDeposit(context, dialog, btnConfirm, amount, onSuccess);
                return;
            }

            String desc = "Nạp tiền từ " + sourceBank + (sourceAcc.isEmpty() ? "" : " (" + sourceAcc + ")");
            executeBalanceUpdate(
                    context,
                    dialog,
                    btnConfirm,
                    new DepositWithdrawRequest("DEPOSIT", amount, desc),
                    "Nạp tiền thành công!",
                    onSuccess
            );
        });

        dialog.show();
    }

    public static void showWithdrawDialog(Context context, String currentBalanceText, Runnable onSuccess) {
        if (context == null) return;
        Dialog dialog = createStyledDialog(context, R.layout.dialog_withdraw);

        TextView tvSourceBalance = dialog.findViewById(R.id.tvSourceBalance);
        LinearLayout llBankSelection = dialog.findViewById(R.id.llBankSelection);
        TextView tvBankName = dialog.findViewById(R.id.tvBankName);
        EditText etBankAccount = dialog.findViewById(R.id.etBankAccountNumber);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        if (tvSourceBalance != null) {
            tvSourceBalance.setText(currentBalanceText);
        }
        if (llBankSelection != null && tvBankName != null) {
            llBankSelection.setOnClickListener(v -> showBankPicker(context, tvBankName));
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String targetBank = tvBankName != null ? tvBankName.getText().toString().trim() : "Ngân hàng";
            String targetAcc = etBankAccount != null ? etBankAccount.getText().toString().trim() : "";
            double amount = parsePositiveAmount(etAmount);

            if (targetAcc.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập số tài khoản nhận.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (amount <= 0) {
                Toast.makeText(context, "Vui lòng nhập số tiền hợp lệ (> 0).", Toast.LENGTH_SHORT).show();
                return;
            }

            String desc = "Rút tiền về " + targetBank + " (" + targetAcc + ")";
            executeBalanceUpdate(
                    context,
                    dialog,
                    btnConfirm,
                    new DepositWithdrawRequest("WITHDRAW", amount, desc),
                    "Rút tiền thành công!",
                    onSuccess
            );
        });

        dialog.show();
    }

    private static void executeBalanceUpdate(Context context,
                                             Dialog dialog,
                                             Button btnConfirm,
                                             DepositWithdrawRequest request,
                                             String successMessage,
                                             Runnable onSuccess) {
        btnConfirm.setEnabled(false);
        ApiClient.getService().depositOrWithdraw(request).enqueue(new Callback<ApiResponse<BalanceUpdateData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<BalanceUpdateData>> call,
                                   @NonNull Response<ApiResponse<BalanceUpdateData>> response) {
                btnConfirm.setEnabled(true);
                ApiResponse<BalanceUpdateData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess()) {
                    dialog.dismiss();
                    Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    String msg = ApiErrorHelper.extractMessage(response, "Giao dịch thất bại.");
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<BalanceUpdateData>> call, @NonNull Throwable t) {
                btnConfirm.setEnabled(true);
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static Dialog createStyledDialog(Context context, int layoutResId) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutResId);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        return dialog;
    }

    private static void startStripeDeposit(Context context,
                                           Dialog depositDialog,
                                           Button btnConfirm,
                                           double amount,
                                           Runnable onSuccess) {
        if (amount < 20000) {
            Toast.makeText(context, "Nạp qua Stripe tối thiểu 20.000 VND.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnConfirm.setEnabled(false);
        ApiClient.getService().createStripeCheckout(new StripeCheckoutRequest(amount))
                .enqueue(new Callback<ApiResponse<StripeCheckoutData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<StripeCheckoutData>> call,
                                           @NonNull Response<ApiResponse<StripeCheckoutData>> response) {
                        btnConfirm.setEnabled(true);
                        ApiResponse<StripeCheckoutData> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                            depositDialog.dismiss();
                            showStripeWebViewDialog(context, body.getData(), onSuccess);
                        } else {
                            String msg = ApiErrorHelper.extractMessage(response, "Không khởi tạo được cổng Stripe.");
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<StripeCheckoutData>> call, @NonNull Throwable t) {
                        btnConfirm.setEnabled(true);
                        Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void showStripeWebViewDialog(Context context,
                                                StripeCheckoutData data,
                                                Runnable onSuccess) {
        Dialog webDialog = new Dialog(context);
        webDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WebView webView = new WebView(context);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        final boolean[] handled = {false};
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (handled[0] || url == null) return;
                if (data.getSuccessUrlPrefix() != null && url.startsWith(data.getSuccessUrlPrefix())) {
                    handled[0] = true;
                    webDialog.dismiss();
                    Toast.makeText(context, "Nạp tiền qua Stripe thành công!", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else if (data.getCancelUrlPrefix() != null && url.startsWith(data.getCancelUrlPrefix())) {
                    handled[0] = true;
                    webDialog.dismiss();
                    Toast.makeText(context, "Đã hủy thanh toán Stripe.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        webView.loadUrl(data.getCheckoutUrl());
        webDialog.setContentView(webView);
        if (webDialog.getWindow() != null) {
            webDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        webDialog.show();
    }

    private static void showBankPicker(Context context, TextView targetTextView) {
        showBankPicker(context, targetTextView, BANKS);
    }

    private static void showBankPicker(Context context, TextView targetTextView, String[] options) {
        new AlertDialog.Builder(context)
                .setTitle("Chọn ngân hàng")
                .setItems(options, (d, which) -> targetTextView.setText(options[which]))
                .show();
    }

    private static double parsePositiveAmount(EditText editText) {
        if (editText == null) return -1;
        String raw = editText.getText().toString().trim().replace(",", "").replace(" ", "");
        if (raw.isEmpty()) return -1;
        try {
            double val = Double.parseDouble(raw);
            return val > 0 ? val : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
