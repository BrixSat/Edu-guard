package com.example.eduguard.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class DataStoreManager {

    private static final String PREF_NAME = "edu_guard_prefs";

    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_THEME = "theme_dark";

    private static DataStoreManager instance;
    private final SharedPreferences prefs;

    // -----------------------------
    // Constructor
    // -----------------------------
    public DataStoreManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // -----------------------------
    // Singleton Instance Provider
    // -----------------------------
    public static synchronized DataStoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataStoreManager(context.getApplicationContext());
        }
        return instance;
    }

    // -----------------------------
    // LOGIN SESSION HANDLING
    // -----------------------------
    public void saveSession(String token, String role, String userId) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_ROLE, role)
                .putString(KEY_USER_ID, userId)
                .apply();
    }

    public boolean isLoggedIn() {
        return !getTokenValue().isEmpty();
    }

    public String getTokenValue() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public String getUserIdValue() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getRoleValue() {
        return prefs.getString(KEY_ROLE, "");
    }

    // OPTIONAL: Get whole user session object
    public UserSession getUserSession() {
        return new UserSession(
                getTokenValue(),
                getRoleValue(),
                getUserIdValue()
        );
    }

    // -----------------------------
    // THEME MODE
    // -----------------------------
    public void saveThemeMode(boolean isDark) {
        prefs.edit().putBoolean(KEY_THEME, isDark).apply();
    }

    public boolean getThemeMode() {
        return prefs.getBoolean(KEY_THEME, false);
    }

    // -----------------------------
    // LOGOUT
    // -----------------------------
    public void clearSession() {
        prefs.edit().clear().apply();
    }

    // -----------------------------
    // SESSION MODEL CLASS
    // -----------------------------
    public static class UserSession {
        public final String token;
        public final String role;
        public final String userId;

        public UserSession(String token, String role, String userId) {
            this.token = token;
            this.role = role;
            this.userId = userId;
        }
    }
}
