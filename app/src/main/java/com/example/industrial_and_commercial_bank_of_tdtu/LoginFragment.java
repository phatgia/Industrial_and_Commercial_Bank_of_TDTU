package com.example.industrial_and_commercial_bank_of_tdtu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.User;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private EditText edPhone, edPwd;
    private Button btnLogin, btnFaceId;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        sessionManager = new SessionManager(requireContext());
        edPhone = view.findViewById(R.id.edPhone);
        edPwd = view.findViewById(R.id.edPwd);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnFaceId = view.findViewById(R.id.btnFaceId);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        btnFaceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.isLoggedIn()) {
                    navigateByRole(sessionManager.getRole(), sessionManager.getFullName());
                } else {
                    Toast.makeText(requireContext(),
                            "Vui lòng đăng nhập bằng SĐT/Email và mật khẩu trước.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void attemptLogin() {
        String identifier = edPhone.getText().toString().trim();
        String password = edPwd.getText().toString();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập SĐT/Email và mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        ApiClient.getService().login(new LoginRequest(identifier, password)).enqueue(new Callback<ApiResponse<LoginData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<LoginData>> call, @NonNull Response<ApiResponse<LoginData>> response) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);

                ApiResponse<LoginData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                    User user = body.getData().getUser();
                    if (user != null) {
                        sessionManager.saveUser(user);
                        navigateByRole(user.getRole(), user.getFullName());
                        return;
                    }
                }

                String errorMsg = (body != null && body.getMessage() != null)
                        ? body.getMessage()
                        : "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.";
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<LoginData>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                btnLogin.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateByRole(String role, String fullName) {
        Toast.makeText(requireContext(), "Chào mừng " + fullName, Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if ("OFFICER".equalsIgnoreCase(role)) {
                Toast.makeText(requireContext(),
                        "Đăng nhập với quyền Nhân viên ngân hàng (Officer).",
                        Toast.LENGTH_LONG).show();
            }
            mainActivity.showMainContent();
        }
    }
}
