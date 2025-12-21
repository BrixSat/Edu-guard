package com.example.eduguard.ui.admin.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.User;
import com.example.eduguard.ui.admin.adapters.AssignStudentAdapter;
import com.example.eduguard.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class AssignStudentsActivity extends AppCompatActivity {

    private RecyclerView rvStudents;
    private UserViewModel userViewModel;
    private AssignStudentAdapter adapter;

    private String mentorId;
    private final List<User> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_students);

        mentorId = getIntent().getStringExtra("mentorId");

        rvStudents = findViewById(R.id.rvAssignStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        adapter = new AssignStudentAdapter(studentList, student -> {
            userViewModel.assignStudent(student.getId(), mentorId);
            Toast.makeText(this, "Assigned!", Toast.LENGTH_SHORT).show();
        });

        rvStudents.setAdapter(adapter);

        observeData();

        userViewModel.fetchStudents();
    }

    private void observeData() {
        userViewModel.getStudentsList().observe(this, list -> {
            studentList.clear();
            studentList.addAll(list);
            adapter.notifyDataSetChanged();
        });

        userViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
