package com.example.eduguard.ui.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.eduguard.R;
import com.example.eduguard.data.model.User;
import com.example.eduguard.databinding.FragmentAdminStudentsBinding;
import com.example.eduguard.ui.admin.AddStudentActivity;
import com.example.eduguard.ui.admin.AssignMentorActivity;
import com.example.eduguard.ui.admin.EditStudentActivity;
import com.example.eduguard.ui.admin.adapters.StudentAdapter;
import com.example.eduguard.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminStudentsFragment extends Fragment {

    private FragmentAdminStudentsBinding binding;
    private UserViewModel userViewModel;
    private StudentAdapter adapter;
    private final List<User> studentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAdminStudentsBinding.inflate(inflater, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupRecycler();
        setupObservers();
        setupClickListeners();

        userViewModel.fetchStudents();

        return binding.getRoot();
    }

    private void setupRecycler() {
        adapter = new StudentAdapter(studentList, new StudentAdapter.StudentActionListener() {
            @Override
            public void onEdit(User student) {
                Intent intent = new Intent(getContext(), EditStudentActivity.class);
                intent.putExtra("studentId", student.getId());
                intent.putExtra("phone", student.getPhone());
                startActivity(intent);
            }

            @Override
            public void onDelete(User student) {
                userViewModel.deleteUser(student.getId());
            }

            @Override
            public void onAssignMentor(User student) {
                Intent intent = new Intent(getContext(), AssignMentorActivity.class);
                intent.putExtra("studentId", student.getId());
                startActivity(intent);
            }
        });

        binding.rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvStudents.setAdapter(adapter);
    }

    private void setupObservers() {
        userViewModel.getStudentsList().observe(getViewLifecycleOwner(), list -> {
            studentList.clear();
            studentList.addAll(list);
            adapter.notifyDataSetChanged();
        });

        userViewModel.getDeleteResult().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                userViewModel.fetchStudents();
            }
        });
    }

    private void setupClickListeners() {
        binding.fabAddStudent.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddStudentActivity.class))
        );
    }
}
