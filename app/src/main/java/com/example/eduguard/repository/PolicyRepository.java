package com.example.eduguard.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.data.remote.ApiService;
import com.example.eduguard.data.remote.RetrofitClient;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PolicyRepository {

    private final ApiService apiService;
    private final DataStoreManager dataStore;

    public PolicyRepository(Context context) {
        dataStore = DataStoreManager.getInstance(context);
        apiService = RetrofitClient
                .getRetrofitWithAuth(dataStore.getTokenValue())   // ✅ FIXED
                .create(ApiService.class);
    }

    private String bearer() {
        String t = dataStore.getTokenValue();
        return (t == null || t.isEmpty()) ? "" : "Bearer " + t;
    }

    // ============================
    // Get Student Policy
    // ============================
    public void fetchPolicy(String studentId,
                            MutableLiveData<Policy> result,
                            MutableLiveData<String> error) {

        apiService.getPolicy(studentId, bearer()).enqueue(new Callback<Policy>() {
            @Override
            public void onResponse(Call<Policy> call, Response<Policy> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    error.postValue("Failed to load policy: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Policy> call, Throwable t) {
                error.postValue("Error: " + t.getMessage());
            }
        });
    }

    // ============================
    // Update Policy
    // ============================
    public void updatePolicy(String studentId, Policy policy,
                             MutableLiveData<Boolean> result,
                             MutableLiveData<String> error) {

        apiService.updatePolicy(studentId, bearer(), policy)
                .enqueue(new Callback<Policy>() {
                    @Override
                    public void onResponse(Call<Policy> call, Response<Policy> response) {
                        result.postValue(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Policy> call, Throwable t) {
                        error.postValue("Error: " + t.getMessage());
                    }
                });
    }
}
