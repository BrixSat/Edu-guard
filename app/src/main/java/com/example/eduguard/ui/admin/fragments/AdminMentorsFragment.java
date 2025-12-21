package com.example.eduguard.ui.admin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eduguard.R;
import com.example.eduguard.data.model.User;
import com.example.eduguard.ui.admin.activities.AddMentorActivity;
import com.example.eduguard.ui.admin.activities.AssignStudentsActivity;
import com.example.eduguard.ui.admin.activities.EditMentorActivity;
import com.example.eduguard.ui.admin.adapters.MentorAdapter;
import com.example.eduguard.viewmodel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminMentorsFragment extends Fragment {

    private RecyclerView rvMentors;
    private FloatingActionButton fabAddMentor;

    private MentorAdapter adapter;
    private UserViewModel userViewModel;
    private final List<User> mentorList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_mentors, container, false);

        rvMentors = view.findViewById(R.id.rvMentors);
        fabAddMentor = view.findViewById(R.id.fabAddMentor);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        setupRecycler();
        observeMentors();

        userViewModel.fetchMentors();   // 🔥 Fetch from backend

        fabAddMentor.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddMentorActivity.class))
        );

        return view;
    }

    private void setupRecycler() {
        rvMentors.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MentorAdapter(mentorList, new MentorAdapter.MentorActionListener() {
            @Override
            public void onEdit(User mentor) {
                Intent intent = new Intent(getContext(), EditMentorActivity.class);
                intent.putExtra("mentorId", mentor.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(User mentor) {
                userViewModel.deleteUser(mentor.getId());
                Toast.makeText(getContext(), "Deleting...", Toast.LENGTH_SHORT).show();
                userViewModel.fetchMentors();
            }

            @Override
            public void onAssign(User mentor) {
                Intent intent = new Intent(getContext(), AssignStudentsActivity.class);
                intent.putExtra("mentorId", mentor.getId());
                startActivity(intent);
            }
        });

        rvMentors.setAdapter(adapter);
    }

    private void observeMentors() {
        userViewModel.getMentorsList().observe(getViewLifecycleOwner(), mentors -> {
            mentorList.clear();
            mentorList.addAll(mentors);
            adapter.notifyDataSetChanged();
        });

        userViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show()
        );
    }
}
