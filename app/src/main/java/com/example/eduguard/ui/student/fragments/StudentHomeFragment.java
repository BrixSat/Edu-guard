package com.example.eduguard.ui.student.fragments;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.eduguard.R;
import com.example.eduguard.data.model.Policy;
import com.example.eduguard.policy.PolicyStore;

import java.util.Calendar;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private TextView tvTodayUsage, tvSleepModeStatus;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        tvTodayUsage = view.findViewById(R.id.tvTodayUsed);
        tvSleepModeStatus = view.findViewById(R.id.tvSleepModeStatus);

        // Check usage access permission first
        if (!hasUsageAccess(requireContext())) {
            Toast.makeText(getContext(), "Enable Usage Access for EduGuard", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } else {
            loadUsageToday();
        }

        loadSleepModeStatus();

        return view;
    }

    // ---------------------------------------------------
    // CHECK IF USAGE ACCESS IS GRANTED
    // ---------------------------------------------------
    private boolean hasUsageAccess(Context ctx) {
        AppOpsManager appOps = (AppOpsManager) ctx.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                ctx.getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    // ---------------------------------------------------
    // LOAD TODAY'S SCREEN TIME
    // ---------------------------------------------------
    private void loadUsageToday() {
        long totalMs = getTotalScreenTimeToday(requireContext());
        long minutes = totalMs / (1000 * 60);

        tvTodayUsage.setText("Today Usage: " + minutes + " min");
    }

    private long getTotalScreenTimeToday(Context ctx) {
        UsageStatsManager usm = (UsageStatsManager) ctx.getSystemService(Context.USAGE_STATS_SERVICE);

        long start = getStartOfDay();
        long end = System.currentTimeMillis();

        List<UsageStats> stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                start,
                end
        );

        if (stats == null || stats.isEmpty()) return 0;

        long total = 0;

        for (UsageStats s : stats) {
            // Ignore system apps with 0 foreground usage
            if (s.getTotalTimeInForeground() > 0) {
                total += s.getTotalTimeInForeground();
            }
        }
        return total;
    }

    private long getStartOfDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTimeInMillis();
    }

    // ---------------------------------------------------
    // LOAD SLEEP MODE STATUS FROM POLICY
    // ---------------------------------------------------
    private void loadSleepModeStatus() {
        Policy policy = new PolicyStore(requireContext()).getPolicy();
        if (policy == null || policy.getSleepMode() == null) {
            tvSleepModeStatus.setText("Sleep Mode: Not Configured");
            return;
        }

        Policy.SleepMode sm = policy.getSleepMode();
        boolean enabled = sm.isEnabled();

        if (!enabled) {
            tvSleepModeStatus.setText("Sleep Mode: Disabled");
            return;
        }

        String start = sm.getStartTime();
        String end = sm.getEndTime();
        boolean isActiveNow = isSleepModeActive(start, end);

        if (isActiveNow) {
            tvSleepModeStatus.setText("Sleep Mode: ACTIVE now (" + start + " - " + end + ")");
        } else {
            tvSleepModeStatus.setText("Sleep Mode: Scheduled (" + start + " - " + end + ")");
        }
    }

    // ---------------------------------------------------
    // CHECK IF CURRENT TIME IS INSIDE SLEEP MODE WINDOW
    // ---------------------------------------------------
    private boolean isSleepModeActive(String start, String end) {

        String[] s = start.split(":");
        String[] e = end.split(":");

        int startH = Integer.parseInt(s[0]);
        int startM = Integer.parseInt(s[1]);

        int endH = Integer.parseInt(e[0]);
        int endM = Integer.parseInt(e[1]);

        Calendar now = Calendar.getInstance();
        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();

        startCal.set(Calendar.HOUR_OF_DAY, startH);
        startCal.set(Calendar.MINUTE, startM);

        endCal.set(Calendar.HOUR_OF_DAY, endH);
        endCal.set(Calendar.MINUTE, endM);

        // Case: Sleep spans midnight (22:00 → 06:00)
        if (endCal.before(startCal)) {
            endCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return now.after(startCal) && now.before(endCal);
    }
}
