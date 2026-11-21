package com.example.industrial_and_commercial_bank_of_tdtu.network;

import com.example.industrial_and_commercial_bank_of_tdtu.model.AccountsData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ApiResponse;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BalanceUpdateData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.BranchesData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ChatData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.ChatRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CreateCustomerRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.CustomersData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.DepositWithdrawRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.EkycUploadData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.EkycUploadRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.LoginRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.OcrCccdData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.PayBillRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.StripeCheckoutData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.StripeCheckoutRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransactionsData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferConfirmData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferOtpData;
import com.example.industrial_and_commercial_bank_of_tdtu.model.TransferRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UpdatePolicyRequest;
import com.example.industrial_and_commercial_bank_of_tdtu.model.UtilityBillsData;

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

    @POST("transactions/transfer.php")
    Call<ApiResponse<TransferOtpData>> requestTransferOtp(@Body TransferRequest request);

    @POST("transactions/transfer.php")
    Call<ApiResponse<TransferConfirmData>> confirmTransfer(@Body TransferRequest request);

    @POST("transactions/deposit_withdraw.php")
    Call<ApiResponse<BalanceUpdateData>> depositOrWithdraw(@Body DepositWithdrawRequest request);

    @POST("transactions/stripe_checkout.php")
    Call<ApiResponse<StripeCheckoutData>> createStripeCheckout(@Body StripeCheckoutRequest request);

    @GET("utilities/bills.php")
    Call<ApiResponse<UtilityBillsData>> getUtilityBills(
            @Query("service_type") String serviceType,
            @Query("bill_code") String billCode
    );

    @POST("utilities/pay.php")
    Call<ApiResponse<BalanceUpdateData>> payUtilityBill(@Body PayBillRequest request);

    @POST("utilities/topup.php")
    Call<ApiResponse<BalanceUpdateData>> phoneTopUp(@Body Map<String, Object> request);

    @POST("ekyc/upload.php")
    Call<ApiResponse<EkycUploadData>> uploadEkyc(@Body EkycUploadRequest request);

    @POST("ekyc/ocr.php")
    Call<ApiResponse<OcrCccdData>> ocrCccd(@Body Map<String, String> body);

    @GET("officer/customers.php")
    Call<ApiResponse<CustomersData>> getCustomers();

    @POST("officer/customers.php")
    Call<ApiResponse<Map<String, Object>>> createCustomer(@Body CreateCustomerRequest request);

    @POST("officer/customers.php")
    Call<ApiResponse<Map<String, Object>>> updateCustomer(@Body Map<String, Object> request);

    @POST("officer/update_policy.php")
    Call<ApiResponse<Map<String, Object>>> updatePolicy(@Body UpdatePolicyRequest request);

    @GET("branches/list.php")
    Call<ApiResponse<BranchesData>> getBranches();

    @POST("chat/ask.php")
    Call<ApiResponse<ChatData>> askChatbot(@Body ChatRequest request);
}
