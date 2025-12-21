package com.example.eduguard.ui.student.fragments;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eduguard.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentUsageChartFragment extends Fragment {

    private BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usage_chart, container, false);

        barChart = view.findViewById(R.id.barChart);
        loadAppUsageChart();

        return view;
    }

    private void loadAppUsageChart() {
        long start = getStartOfDay();
        long now = System.currentTimeMillis();

        UsageStatsManager usm =
                (UsageStatsManager) requireContext().getSystemService(Context.USAGE_STATS_SERVICE);

        List<UsageStats> stats =
                usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, now);

        ArrayList<BarEntry> entries = new ArrayList<>();
        int index = 0;

        for (UsageStats s : stats) {
            long minutes = s.getTotalTimeInForeground() / 1000 / 60;
            if (minutes == 0) continue;

            entries.add(new BarEntry(index++, minutes));
        }

        BarDataSet set = new BarDataSet(entries, "App Usage (min)");
        BarData data = new BarData(set);

        barChart.setData(data);
        barChart.invalidate();
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private long getStartOfDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }
}
