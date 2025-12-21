package com.example.eduguard.ui.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eduguard.databinding.ActivityAssignMentorBinding;
import com.example.eduguard.ui.admin.adapters.AssignMentorAdapter;
import com.example.eduguard.viewmodel.UserViewModel;

public class AssignMentorActivity extends AppCompatActivity {

    private ActivityAssignMentorBinding binding;
    private UserViewModel userViewModel;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAssignMentorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        studentId = getIntent().getStringExtra("studentId");

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupRecycler();
        observeMentors();

        userViewModel.fetchMentors();
    }

    private void observeMentors() {
    }

    private void setupRecycler() {
        binding.rvAssignMentor.setLayoutManager(new LinearLayoutManager(this));
    }

//    private void observeMentors() {
//        userViewModel.getMentorsList().observe(this, mentors -> {
//            AssignMentorAdapter adapter = new AssignMentorAdapter(mentors, mentor -> {
//                userViewModel.assignMentor(studentId, mentor.getId());
//            });
//
//            binding.rvAssignMentor.setAdapter(adapter);
//        });
//
//        userViewModel.getAssignResult().observe(this, success -> {
//            if (success != null && success) {
//                Toast.makeText(this, "Mentor assigned!", Toast.LENGTH_SHORT).show();
//                finish();
//            }
//        });
//    }
}
