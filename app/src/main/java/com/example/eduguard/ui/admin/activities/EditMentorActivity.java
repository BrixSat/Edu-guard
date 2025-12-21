package com.example.eduguard.ui.admin.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eduguard.R;
import com.example.eduguard.viewmodel.UserViewModel;

public class EditMentorActivity extends AppCompatActivity {

    private EditText etPhone;
    private Button btnUpdate;

    private UserViewModel userViewModel;
    private String mentorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mentor);

        mentorId = getIntent().getStringExtra("mentorId");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etPhone = findViewById(R.id.etEditPhone);
        btnUpdate = findViewById(R.id.btnUpdateMentor);

        btnUpdate.setOnClickListener(v -> updateMentor());
    }

    private void updateMentor() {
    }


}
