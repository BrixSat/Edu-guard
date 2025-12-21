package com.example.eduguard.ui.admin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.eduguard.R;

public class AdminDashboardFragment extends Fragment {

    private TextView tvStudentsCount, tvMentorsCount, tvPendingRequests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        tvStudentsCount = view.findViewById(R.id.tvStudentsCount);
        tvMentorsCount = view.findViewById(R.id.tvMentorsCount);
        tvPendingRequests = view.findViewById(R.id.tvPendingRequests);

        // Dummy values for now
        tvStudentsCount.setText("24");
        tvMentorsCount.setText("5");
        tvPendingRequests.setText("7");

        return view;
    }
}
