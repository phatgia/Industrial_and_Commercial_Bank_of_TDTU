package com.example.industrial_and_commercial_bank_of_tdtu.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton cung cap Retrofit client ket noi toi PHP API tren hosting phatg.id.vn.
 */
public final class ApiClient {

    // Base URL phai ket thuc bang dau '/'
    public static final String BASE_URL = "https://phatg.id.vn/api/";

    private static volatile Retrofit retrofit;

    private ApiClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static BankApiService getService() {
        return getClient().create(BankApiService.class);
    }
}
