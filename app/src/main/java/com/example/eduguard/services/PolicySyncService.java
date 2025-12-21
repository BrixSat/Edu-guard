package com.example.eduguard.services;

import android.content.Context;
import android.util.Log;

import com.example.eduguard.data.model.Policy;
import com.example.eduguard.data.remote.RequestRepository;
import com.example.eduguard.mdm.SleepScheduler;
import com.example.eduguard.mdm.WhitelistEnforcer;
import com.example.eduguard.policy.PolicyStore;

public class PolicySyncService {

    private static final String TAG = "PolicySyncService";

    // ------------------------------------------------------------
    // SYNC POLICY FROM SERVER → APPLY WHITELIST + SLEEP MODE
    // ------------------------------------------------------------
    public static void syncNow(Context ctx) {
        if (ctx == null) return;

        PolicyStore store = new PolicyStore(ctx);
        String studentId = store.getStudentId();

        if (studentId == null || studentId.isEmpty()) {
            Log.e(TAG, "No studentId found. Cannot sync policy.");
            return;
        }

        RequestRepository repo = new RequestRepository(ctx);

        // Fetch latest policy from server
        repo.fetchPolicy(studentId, new RequestRepository.PolicyCallback() {
            @Override
            public void onSuccess(Policy policy) {

                Log.d(TAG, "Policy fetched successfully.");

                // Save new policy
                PolicyStore policyStore = new PolicyStore(ctx);
                policyStore.savePolicy(policy);

                // Apply whitelist policy
                new WhitelistEnforcer(ctx).apply(policy);

                // Apply sleep mode policy
                new SleepScheduler(ctx).apply(policy.getSleepMode());

                Log.d(TAG, "Policy applied successfully.");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to fetch policy: " + error);
            }
        });
    }
}
