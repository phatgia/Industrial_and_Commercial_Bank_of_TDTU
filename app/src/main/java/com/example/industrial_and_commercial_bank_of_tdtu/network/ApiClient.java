package com.example.industrial_and_commercial_bank_of_tdtu.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton cung cap Retrofit client va tu dong gan Bearer Token vao header moi request.
 */
public final class ApiClient {

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

                    Interceptor authInterceptor = new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request original = chain.request();
                            if (original.header("Authorization") != null) {
                                return chain.proceed(original);
                            }
                            String token = new SessionManager().getToken();
                            if (token != null && !token.isEmpty()) {
                                Request authenticated = original.newBuilder()
                                        .header("Authorization", "Bearer " + token)
                                        .build();
                                return chain.proceed(authenticated);
                            }
                            return chain.proceed(original);
                        }
                    };

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                            .addInterceptor(authInterceptor)
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
