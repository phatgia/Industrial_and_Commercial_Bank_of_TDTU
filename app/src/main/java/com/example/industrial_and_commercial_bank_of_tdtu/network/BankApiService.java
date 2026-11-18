package com.example.industrial_and_commercial_bank_of_tdtu.network;

import com.example.industrial_and_commercial_bank_of_tdtu.model.AccountsData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BranchesData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionsData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Dinh nghia cac endpoint giao tiep voi Backend PHP (tu dong gui kem Bearer Token qua ApiClient).
 */
public interface BankApiService {

    @GET("health.php")
    Call<ApiResponse<Map<String, Object>>> checkHealth();

    @POST("auth/login.php")
    Call<ApiResponse<LoginData>> login(@Body LoginRequest request);

    @POST("auth/logout.php")
    Call<ApiResponse<Object>> logout(@Header("Authorization") String bearerHeader);

    @GET("accounts/list.php")
    Call<ApiResponse<AccountsData>> getAccounts();

    @GET("transactions/history.php")
    Call<ApiResponse<TransactionsData>> getTransactionHistory(@Query("limit") int limit);

    @GET("branches/list.php")
    Call<ApiResponse<BranchesData>> getBranches();
}
