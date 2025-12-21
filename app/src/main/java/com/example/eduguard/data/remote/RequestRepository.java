package com.example.eduguard.data.remote;

import android.content.Context;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.ExtraTimeRequest;
import com.example.eduguard.data.model.Policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestRepository {

    // -------------------------------
    // Single object callback
    // -------------------------------
    public interface CallbackSingle {
        void onSuccess(ExtraTimeRequest response);
        void onError(String error);
    }

    // -------------------------------
    // List callback
    // -------------------------------
    public interface CallbackList {
        void onSuccess(List<ExtraTimeRequest> list);
        void onError(String error);
    }

    // -------------------------------
    // Policy callback
    // -------------------------------
    public interface PolicyCallback {
        void onSuccess(Policy policy);
        void onError(String error);
    }

    private final ApiService api;
    private final Context ctx;

    public RequestRepository(Context context) {
        this.ctx = context.getApplicationContext();
        this.api = RetrofitClient.getClient(ctx).create(ApiService.class);
    }

    private String getBearer() {
        String token = DataStoreManager.getInstance(ctx).getTokenValue();
        return "Bearer " + token;
    }

    // ---------------------------------------------------------
    // STUDENT → submit extra time request
    // ---------------------------------------------------------
    public void postExtraTimeRequest(ExtraTimeRequest req, CallbackSingle cb) {
        api.postExtraTimeRequest(getBearer(), req)
                .enqueue(new Callback<ExtraTimeRequest>() {
                    @Override
                    public void onResponse(Call<ExtraTimeRequest> call, Response<ExtraTimeRequest> resp) {
                        if (resp.isSuccessful() && resp.body() != null)
                            cb.onSuccess(resp.body());
                        else
                            cb.onError("Server error: " + resp.code());
                    }

                    @Override
                    public void onFailure(Call<ExtraTimeRequest> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // ADMIN / MENTOR → get all requests
    // Backend automatically filters by user role
    // ---------------------------------------------------------
    public void getAllRequests(CallbackList cb) {
        api.getRequests(getBearer())
                .enqueue(new Callback<List<ExtraTimeRequest>>() {
                    @Override
                    public void onResponse(Call<List<ExtraTimeRequest>> call, Response<List<ExtraTimeRequest>> resp) {
                        if (resp.isSuccessful() && resp.body() != null)
                            cb.onSuccess(resp.body());
                        else
                            cb.onError("Server error: " + resp.code());
                    }

                    @Override
                    public void onFailure(Call<List<ExtraTimeRequest>> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // APPROVE / REJECT request (one API)
    // ---------------------------------------------------------
    public void updateRequestStatus(String reqId, String status, CallbackSingle cb) {
        Map<String, String> body = new HashMap<>();
        body.put("status", status);

        api.updateRequestStatus(getBearer(), reqId, body)
                .enqueue(new Callback<ExtraTimeRequest>() {
                    @Override
                    public void onResponse(Call<ExtraTimeRequest> call, Response<ExtraTimeRequest> resp) {
                        if (resp.isSuccessful() && resp.body() != null)
                            cb.onSuccess(resp.body());
                        else
                            cb.onError("Server error: " + resp.code());
                    }

                    @Override
                    public void onFailure(Call<ExtraTimeRequest> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }

    // ---------------------------------------------------------
    // Fetch POLICY (student device sync)
    // ---------------------------------------------------------
    public void fetchPolicy(String studentId, PolicyCallback cb) {
        api.getPolicy(studentId, getBearer())
                .enqueue(new Callback<Policy>() {
                    @Override
                    public void onResponse(Call<Policy> call, Response<Policy> resp) {
                        if (resp.isSuccessful() && resp.body() != null)
                            cb.onSuccess(resp.body());
                        else
                            cb.onError("Server error: " + resp.code());
                    }

                    @Override
                    public void onFailure(Call<Policy> call, Throwable t) {
                        cb.onError(t.getMessage());
                    }
                });
    }
}
