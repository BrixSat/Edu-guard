package com.example.eduguard.data.remote;

import android.content.Context;

import com.example.eduguard.EduGuardApp;
import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofitNoAuth;
    private static Retrofit retrofitWithAuth;

    // --------------------------------------------------------------------------
    // Retrofit WITHOUT Authorization header
    // --------------------------------------------------------------------------
    public static Retrofit getRetrofitWithoutAuth() {
        if (retrofitNoAuth == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            retrofitNoAuth = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitNoAuth;
    }

    // --------------------------------------------------------------------------
    // Retrofit WITH Authorization header
    // --------------------------------------------------------------------------
    public static Retrofit getRetrofitWithAuth(final String token) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        retrofitWithAuth = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofitWithAuth;
    }

    // --------------------------------------------------------------------------
    // NEW: Auto-select Retrofit client based on stored token
    // --------------------------------------------------------------------------
    public static Retrofit getClient(Context context) {

        // Fallback if context == null
        if (context == null) {
            context = EduGuardApp.getInstance().getApplicationContext();
        }

        String token = DataStoreManager
                .getInstance(context)
                .getTokenValue();

        if (token == null || token.trim().isEmpty()) {
            return getRetrofitWithoutAuth();
        }

        return getRetrofitWithAuth(token);
    }


}
