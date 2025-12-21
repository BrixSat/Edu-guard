package com.example.eduguard.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eduguard.databinding.ActivityEditStudentBinding;
import com.example.eduguard.viewmodel.UserViewModel;

public class EditStudentActivity extends AppCompatActivity {

    private ActivityEditStudentBinding binding;
    private UserViewModel userViewModel;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        studentId = getIntent().getStringExtra("studentId");
        String phone = getIntent().getStringExtra("phone");

        binding.etEditPhone.setText(phone);

        binding.btnUpdateStudent.setOnClickListener(v -> updateStudent());

        userViewModel.getUpdateResult().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateStudent() {
        String phone = binding.etEditPhone.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.updateUser(studentId, phone);
    }
}
