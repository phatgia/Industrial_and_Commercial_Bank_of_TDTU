package com.example.industrial_and_commercial_bank_of_tdtu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.EkycUploadData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.EkycUploadRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.OcrCccdData;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiClient;
import com.example.industrial_and_commercial_bank_of_tdtu.network.ApiErrorHelper;
import com.example.industrial_and_commercial_bank_of_tdtu.network.SessionManager;
import com.google.android.material.button.MaterialButton;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EkycActivity extends AppCompatActivity {

    private static final int MODE_FRONT = 1;
    private static final int MODE_BACK = 2;
    private static final int MODE_FACE = 3;

    private int captureMode = MODE_FACE;
    private Bitmap frontBitmap, backBitmap, faceBitmap;
    private ImageView imgCccdFront, imgCccdBack, imgFacePreview;
    private EditText edCccdNumber, edFullName, edDob, edAddress;
    private MaterialButton btnSubmitEkyc;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap == null) return;
                if (captureMode == MODE_FRONT) {
                    frontBitmap = bitmap;
                    if (imgCccdFront != null) imgCccdFront.setImageBitmap(bitmap);
                    runCccdOcr(bitmap);
                } else if (captureMode == MODE_BACK) {
                    backBitmap = bitmap;
                    if (imgCccdBack != null) imgCccdBack.setImageBitmap(bitmap);
                } else {
                    faceBitmap = bitmap;
                    if (imgFacePreview != null) imgFacePreview.setImageBitmap(bitmap);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ekyc);

        sessionManager = new SessionManager(this);

        ImageButton btnBackEkyc = findViewById(R.id.btnBackEkyc);
        btnBackEkyc.setOnClickListener(v -> finish());

        imgCccdFront = findViewById(R.id.imgCccdFront);
        imgCccdBack = findViewById(R.id.imgCccdBack);
        imgFacePreview = findViewById(R.id.imgFacePreview);

        edCccdNumber = findViewById(R.id.edCccdNumber);
        edFullName = findViewById(R.id.edFullName);
        edDob = findViewById(R.id.edDob);
        edAddress = findViewById(R.id.edAddress);

        if (edFullName != null && !sessionManager.getFullName().isEmpty()) {
            edFullName.setText(sessionManager.getFullName());
        }

        View btnCaptureFront = findViewById(R.id.btnCaptureFront);
        btnCaptureFront.setOnClickListener(v -> {
            captureMode = MODE_FRONT;
            cameraLauncher.launch(null);
        });

        View btnCaptureBack = findViewById(R.id.btnCaptureBack);
        btnCaptureBack.setOnClickListener(v -> {
            captureMode = MODE_BACK;
            cameraLauncher.launch(null);
        });

        View btnScanFace = findViewById(R.id.btnScanFace);
        btnScanFace.setOnClickListener(v -> {
            captureMode = MODE_FACE;
            cameraLauncher.launch(null);
        });

        btnSubmitEkyc = findViewById(R.id.btnSubmitEkyc);
        btnSubmitEkyc.setOnClickListener(v -> submitEkyc());
    }

    private void runCccdOcr(Bitmap frontBmp) {
        String frontB64 = encodeBitmapToBase64(frontBmp);
        ApiClient.getService().ocrCccd(Collections.singletonMap("id_front_base64", frontB64))
                .enqueue(new Callback<ApiResponse<OcrCccdData>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<OcrCccdData>> call,
                                           @NonNull Response<ApiResponse<OcrCccdData>> response) {
                        if (isFinishing() || isDestroyed()) return;
                        ApiResponse<OcrCccdData> body = response.body();
                        if (response.isSuccessful() && body != null && body.isSuccess() && body.getData() != null) {
                            OcrCccdData d = body.getData();
                            if (edCccdNumber != null && d.getCccdNumber() != null && !d.getCccdNumber().isEmpty()) {
                                edCccdNumber.setText(d.getCccdNumber());
                            }
                            if (edFullName != null && d.getFullName() != null && !d.getFullName().isEmpty()) {
                                edFullName.setText(d.getFullName());
                            }
                            if (edDob != null && d.getDob() != null && !d.getDob().isEmpty()) {
                                edDob.setText(d.getDob());
                            }
                            if (edAddress != null && d.getAddress() != null && !d.getAddress().isEmpty()) {
                                edAddress.setText(d.getAddress());
                            }
                            Toast.makeText(EkycActivity.this, "Đã đọc thông tin CCCD tự động!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<OcrCccdData>> call, @NonNull Throwable t) {
                        // Cho phep nguoi dung tu nhap tay neu chua co GEMINI_API_KEY
                    }
                });
    }

    private void submitEkyc() {
        if (faceBitmap == null) {
            Toast.makeText(this, "Vui lòng chụp ảnh khuôn mặt trước khi gửi.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitEkyc.setEnabled(false);
        String faceB64 = encodeBitmapToBase64(faceBitmap);
        String frontB64 = frontBitmap != null ? encodeBitmapToBase64(frontBitmap) : null;
        String backB64 = backBitmap != null ? encodeBitmapToBase64(backBitmap) : null;

        EkycUploadRequest req = new EkycUploadRequest(faceB64, frontB64, backB64);
        ApiClient.getService().uploadEkyc(req).enqueue(new Callback<ApiResponse<EkycUploadData>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<EkycUploadData>> call,
                                   @NonNull Response<ApiResponse<EkycUploadData>> response) {
                if (isFinishing() || isDestroyed()) return;
                btnSubmitEkyc.setEnabled(true);
                ApiResponse<EkycUploadData> body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess()) {
                    sessionManager.setEkycVerified(true);
                    Toast.makeText(EkycActivity.this, "Xác thực eKYC thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String msg = ApiErrorHelper.extractMessage(response, "Gửi hồ sơ eKYC thất bại.");
                    Toast.makeText(EkycActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<EkycUploadData>> call, @NonNull Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                btnSubmitEkyc.setEnabled(true);
                Toast.makeText(EkycActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        int maxDim = 480;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap scaled = bitmap;
        if (w > maxDim || h > maxDim) {
            float ratio = Math.min((float) maxDim / w, (float) maxDim / h);
            scaled = Bitmap.createScaledBitmap(bitmap, Math.round(w * ratio), Math.round(h * ratio), true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }
}
