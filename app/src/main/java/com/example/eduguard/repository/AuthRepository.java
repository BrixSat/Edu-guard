package com.example.eduguard.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.LoginResponse;
import com.example.eduguard.data.remote.ApiService;
import com.example.eduguard.data.remote.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;
    private final DataStoreManager dataStoreManager;

    // ✅ FIXED — now accepts context
    public AuthRepository(Context context) {
        apiService = RetrofitClient.getRetrofitWithoutAuth().create(ApiService.class);
        dataStoreManager = DataStoreManager.getInstance(context.getApplicationContext());
    }

    public void login(String email, String password,
                      MutableLiveData<LoginResponse> result,
                      MutableLiveData<String> error) {

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        apiService.login(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    LoginResponse resp = response.body();

                    // Save session locally
                    dataStoreManager.saveSession(
                            resp.getToken(),
                            resp.getRole(),
                            resp.getUserId()
                    );

                    result.postValue(resp);

                } else {
                    error.postValue("Login failed ❌ (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Network Error: " + t.getMessage());
            }
        });
    }
}
