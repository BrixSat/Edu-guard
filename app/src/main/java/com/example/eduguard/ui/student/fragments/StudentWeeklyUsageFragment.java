package com.example.eduguard.ui.student.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.eduguard.R;

import java.util.Calendar;
import java.util.List;

public class StudentWeeklyUsageFragment extends Fragment {

    private TextView tvWeeklyUsage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_weekly_usage, container, false);

        tvWeeklyUsage = v.findViewById(R.id.tvWeeklyUsage);
        loadWeeklyUsage();

        return v;
    }

    private void loadWeeklyUsage() {
        long total = 0;

        for (int i = 0; i < 7; i++) {
            total += getUsageForDay(i);
        }

        long min = total / 1000 / 60;
        tvWeeklyUsage.setText("Weekly Usage: " + min + " min");
    }

    private long getUsageForDay(int daysAgo) {
        UsageStatsManager usm =
                (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar start = Calendar.getInstance();
        start.add(Calendar.DAY_OF_YEAR, -daysAgo);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);

        Calendar end = Calendar.getInstance();
        end.add(Calendar.DAY_OF_YEAR, -daysAgo);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);

        List<UsageStats> stats =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                        start.getTimeInMillis(), end.getTimeInMillis());

        long total = 0;
        for (UsageStats s : stats) {
            total += s.getTotalTimeInForeground();
        }
        return total;
    }
}
