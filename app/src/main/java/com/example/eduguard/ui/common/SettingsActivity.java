package com.example.eduguard.ui.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.databinding.ActivitySettingsBinding;
import com.example.eduguard.ui.auth.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private DataStoreManager dataStoreManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate UI
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataStoreManager = DataStoreManager.getInstance(getApplicationContext());

        setupThemeSwitch();
        setupLogoutButton();
    }

    // ---------------------------------------------
    // THEME SWITCH (LIGHT / DARK MODE)
    // ---------------------------------------------
    private void setupThemeSwitch() {
        boolean isDark = dataStoreManager.getThemeMode();
        binding.switchTheme.setChecked(isDark);

        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Save theme preference
            dataStoreManager.saveThemeMode(isChecked);

            // Apply theme globally
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    // ---------------------------------------------
    // LOGOUT HANDLER
    // ---------------------------------------------
    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {

            // Clear login session
            dataStoreManager.clearSession();

            // Move user to LoginActivity
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        });
    }
}
