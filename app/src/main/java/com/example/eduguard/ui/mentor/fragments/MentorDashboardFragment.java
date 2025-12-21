package com.example.eduguard.ui.mentor.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.eduguard.R;

public class MentorDashboardFragment extends Fragment {

    private TextView tvMentorStudents, tvMentorPendingRequests;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mentor_dashboard, container, false);
        tvMentorStudents = view.findViewById(R.id.tvMentorStudents);
        tvMentorPendingRequests = view.findViewById(R.id.tvMentorPendingRequests);

        // Dummy values
        tvMentorStudents.setText("Students: 8");
        tvMentorPendingRequests.setText("Pending Requests: 3");

        return view;
    }
}
