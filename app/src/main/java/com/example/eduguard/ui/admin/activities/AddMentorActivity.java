package com.example.eduguard.ui.admin.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eduguard.R;
import com.example.eduguard.viewmodel.UserViewModel;

public class AddMentorActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnCreate;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentor);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etName = findViewById(R.id.etMentorName);
        etEmail = findViewById(R.id.etMentorEmail);
        etPhone = findViewById(R.id.etMentorPhone);
        etPassword = findViewById(R.id.etMentorPassword);
        btnCreate = findViewById(R.id.btnCreateMentor);

        btnCreate.setOnClickListener(v -> createMentor());

        userViewModel.getCreateResult().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Mentor created!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void createMentor() {
        userViewModel.createUser(
                etName.getText().toString(),
                etEmail.getText().toString(),
                etPassword.getText().toString(),
                etPhone.getText().toString(),
                "mentor",
                null
        );
    }
}
