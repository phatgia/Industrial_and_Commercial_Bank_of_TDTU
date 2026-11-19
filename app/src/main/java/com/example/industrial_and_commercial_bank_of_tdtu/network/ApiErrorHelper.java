package com.example.industrial_and_commercial_bank_of_tdtu.network;

import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Helper trich xuat thong bao loi tu JSON errorBody khi server tra ve ma HTTP 4xx/5xx.
 */
public final class ApiErrorHelper {

    private static final Gson GSON = new Gson();

    private ApiErrorHelper() {
    }

    public static String extractMessage(Response<?> response, String fallback) {
        if (response == null) {
            return fallback;
        }
        if (response.body() instanceof ApiResponse<?>) {
            String msg = ((ApiResponse<?>) response.body()).getMessage();
            if (msg != null && !msg.trim().isEmpty()) {
                return msg;
            }
        }
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            try {
                String raw = errorBody.string();
                if (!raw.trim().isEmpty()) {
                    ApiResponse<?> parsed = GSON.fromJson(raw, ApiResponse.class);
                    if (parsed != null && parsed.getMessage() != null && !parsed.getMessage().trim().isEmpty()) {
                        return parsed.getMessage();
                    }
                }
            } catch (Exception ignored) {
                // Fall back to default message if errorBody is not valid JSON
            }
        }
        return fallback;
    }
}
