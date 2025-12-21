package com.example.eduguard.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eduguard.databinding.ActivityLoginBinding;
import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.ui.admin.AdminMainActivity;
import com.example.eduguard.ui.mentor.MentorMainActivity;
import com.example.eduguard.ui.student.StudentMainActivity;
import com.example.eduguard.utils.Constants;
import com.example.eduguard.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataStoreManager dataStore = DataStoreManager.getInstance(getApplicationContext());

        // ⭐ AUTO LOGIN CHECK
        if (!dataStore.getTokenValue().isEmpty()) {
            navigateToRole(dataStore.getRoleValue());
            finish();
            return;
        }


        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupObservers();
        setupClicks();
    }

    private void setupObservers() {
        authViewModel.getLoginResponse().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, "Login Successful ✔", Toast.LENGTH_SHORT).show();
                navigateToRole(response.getRole());
                finish();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        authViewModel.getLoading().observe(this, loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE)
        );
    }

    private void setupClicks() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            authViewModel.login(email, password);
        });
    }

    private void navigateToRole(String role) {
        Intent i;
        if (role.equals(Constants.ROLE_ADMIN))
            i = new Intent(this, AdminMainActivity.class);
        else if (role.equals(Constants.ROLE_MENTOR))
            i = new Intent(this, MentorMainActivity.class);
        else
            i = new Intent(this, StudentMainActivity.class);

        startActivity(i);
    }
}
