package com.example.eduguard.policy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.data.model.Policy.AppRule;
import com.example.eduguard.data.model.Policy.SleepMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class PolicyStore {

    private static final String PREF_NAME = "edu_guard_policy_store";
    private static final String KEY_POLICY_JSON = "policy_json";

    private final SharedPreferences prefs;
    private final Gson gson;
    private final Context context;

    public PolicyStore(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    // ---------------------------------------------------------
    // Save policy received from API (full object)
    // ---------------------------------------------------------
    public void savePolicy(Policy policy) {
        prefs.edit()
                .putString(KEY_POLICY_JSON, gson.toJson(policy))
                .apply();
    }

    // ---------------------------------------------------------
    // Get full policy
    // ---------------------------------------------------------
    public Policy getPolicy() {
        String json = prefs.getString(KEY_POLICY_JSON, null);
        if (json == null) return null;

        Type type = new TypeToken<Policy>() {}.getType();
        return gson.fromJson(json, type);
    }

    // ---------------------------------------------------------
    // Get whitelist for allowed apps
    // ---------------------------------------------------------
    public List<AppRule> getAllowedApps() {
        Policy policy = getPolicy();
        if (policy == null || policy.getAllowedApps() == null) {
            return Collections.emptyList();
        }
        return policy.getAllowedApps();
    }

    // ---------------------------------------------------------
    // Get allowed package names only
    // ---------------------------------------------------------
    public List<String> getAllowedPackages() {
        Policy policy = getPolicy();
        if (policy == null || policy.getAllowedApps() == null) {
            return Collections.emptyList();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return policy.getAllowedApps()
                    .stream()
                    .map(AppRule::getPackageName)
                    .toList();
        }
        return Collections.emptyList();
    }

    // ---------------------------------------------------------
    // Get specific app rule for a package
    // ---------------------------------------------------------
    public AppRule getPolicyForPackage(String packageName) {
        Policy policy = getPolicy();
        if (policy == null || policy.getAllowedApps() == null) return null;

        for (AppRule rule : policy.getAllowedApps()) {
            if (rule.getPackageName().equals(packageName)) {
                return rule;
            }
        }
        return null;
    }

    // ---------------------------------------------------------
    // Sleep mode configuration
    // ---------------------------------------------------------
    public SleepMode getSleepMode() {
        Policy policy = getPolicy();
        if (policy == null) return null;
        return policy.getSleepMode();
    }

    // ---------------------------------------------------------
    // Get currently logged-in student ID
    // (Taken from your DataStoreManager)
    // ---------------------------------------------------------
    public String getStudentId() {
        return DataStoreManager.getInstance(context).getUserIdValue();
    }
}
