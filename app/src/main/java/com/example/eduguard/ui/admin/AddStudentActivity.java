package com.example.eduguard.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eduguard.databinding.ActivityAddStudentBinding;
import com.example.eduguard.viewmodel.UserViewModel;

public class AddStudentActivity extends AppCompatActivity {

    private ActivityAddStudentBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.btnCreateStudent.setOnClickListener(v -> createStudent());

        userViewModel.getCreateResult().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Student created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        userViewModel.getErrorMessage().observe(this, err ->
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show());
    }

    private void createStudent() {
        String name = binding.etStudentName.getText().toString();
        String email = binding.etStudentEmail.getText().toString();
        String phone = binding.etStudentPhone.getText().toString();
        String password = binding.etStudentPassword.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.createUser(name, email, password, phone, "student", null);
    }
}
