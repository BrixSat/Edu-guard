package com.example.eduguard.ui.student.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduguard.R;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.policy.PolicyStore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentRemainingTimeFragment extends Fragment {

    private RecyclerView rvRemaining;

    private List<AppRemainingModel> appUsageList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_remaining_time, container, false);
        rvRemaining = view.findViewById(R.id.rvRemainingApps);
        rvRemaining.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRemainingTimes();

        return view;
    }

    private void loadRemainingTimes() {
        Policy policy = new PolicyStore(requireContext()).getPolicy();
        if (policy == null) return;

        UsageStatsManager usm =
                (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);

        long startOfDay = getStartOfDay();
        long now = System.currentTimeMillis();

        List<UsageStats> stats =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, now);

        for (Policy.AppRule rule : policy.getAllowedApps()) {
            long usedMs = 0;

            for (UsageStats s : stats) {
                if (s.getPackageName().equals(rule.getPackageName())) {
                    usedMs = s.getTotalTimeInForeground();
                }
            }

            int usedMinutes = (int) (usedMs / 1000 / 60);
            int remaining = rule.getDailyLimitMinutes() - usedMinutes;
            if (remaining < 0) remaining = 0;

            appUsageList.add(new AppRemainingModel(
                    rule.getPackageName(),
                    getAppIcon(rule.getPackageName()),
                    usedMinutes,
                    rule.getDailyLimitMinutes(),
                    remaining
            ));
        }

        rvRemaining.setAdapter(new RemainingTimeAdapter(appUsageList));
    }

    private Drawable getAppIcon(String pkg) {
        try {
            return requireContext().getPackageManager().getApplicationIcon(pkg);
        } catch (Exception e) {
            return requireContext().getDrawable(R.drawable.ic_unknown);
        }
    }

    private long getStartOfDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }
}
