package com.example.eduguard.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.eduguard.R;
import com.example.eduguard.data.local.AppDatabase;
import com.example.eduguard.data.local.DataStoreManager;
import com.example.eduguard.data.model.AppUsage;
import com.example.eduguard.data.model.AppUsageDao;
import com.example.eduguard.mdm.DevicePolicyHelper;
import com.example.eduguard.mdm.EduGuardDeviceAdminReceiver;
import com.example.eduguard.policy.PolicyStore;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsageMonitorService extends Service {

    private static final long CHECK_INTERVAL_MS = 2000;

    private UsageStatsManager usageStatsManager;
    private Handler handler;
    private ExecutorService executor;

    private AppUsageDao appUsageDao;
    private PolicyStore policyStore;
    private DevicePolicyHelper dph;

    private String currentPkg = null;
    private long currentStartTime = 0;

    // Logging tag
    private static final String TAG = "UsageMonitorService";

    @Override
    public void onCreate() {
        super.onCreate();

        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        handler = new Handler();
        executor = Executors.newSingleThreadExecutor();

        // Database DAO
        appUsageDao = AppDatabase.getInstance(this).appUsageDao();

        // Policy store (allowed apps, time limits, sleep mode)
        policyStore = new PolicyStore(this);

        // Device owner policy helper
        dph = new DevicePolicyHelper(
                this,
                new ComponentName(this, EduGuardDeviceAdminReceiver.class)
        );

        startForegroundServiceNotification();

        handler.post(checkRunnable);

        Log.d(TAG, "Usage Monitor Service started");
    }

    // ------------------------------
    // Foreground Notification
    // ------------------------------
    private void startForegroundServiceNotification() {
        String channelId = "usage_monitor_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Usage Monitor",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("EduGuard Monitoring")
                .setContentText("Tracking app usage...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(2001, notification);
    }

    // ------------------------------
    // Main Runner
    // ------------------------------
    private final Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String fgApp = getForegroundAppPackage();
                long now = System.currentTimeMillis();

                if (currentPkg == null) {
                    currentPkg = fgApp;
                    currentStartTime = now;
                } else if (!Objects.equals(currentPkg, fgApp)) {
                    long delta = now - currentStartTime;

                    // Save usage for previous app
                    recordUsage(currentPkg, delta);

                    // Switch tracking to new app
                    currentPkg = fgApp;
                    currentStartTime = now;
                }

                // Enforce policy for foreground app
                enforceLimit(fgApp);

            } catch (Exception e) {
                Log.e(TAG, "Error in monitoring loop", e);
            }

            handler.postDelayed(this, CHECK_INTERVAL_MS);
        }
    };

    // ------------------------------
    // Get the Current Foreground App
    // ------------------------------
    private String getForegroundAppPackage() {
        long end = System.currentTimeMillis();
        long begin = end - 5000;

        UsageEvents events = usageStatsManager.queryEvents(begin, end);
        UsageEvents.Event event = new UsageEvents.Event();
        String last = null;

        while (events.hasNextEvent()) {
            events.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                last = event.getPackageName();
            }
        }

        return last;
    }

    // ------------------------------
    // Record Usage into Room Database
    // ------------------------------
    private void recordUsage(String pkg, long delta) {
        if (pkg == null) return;

        executor.execute(() -> {
            String studentId = DataStoreManager.getInstance(this).getUserIdValue();
            String date = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.now().toString();
            }

            AppUsage usage = appUsageDao.getByDateAndPackage(studentId, date, pkg);

            if (usage == null) {
                usage = new AppUsage();
                usage.studentId = studentId;
                usage.date = date;
                usage.packageName = pkg;
                usage.durationMillis = delta;
            } else {
                usage.durationMillis += delta;
            }

            appUsageDao.insertOrUpdate(usage);
        });
    }

    // ------------------------------
    // Apply Time Limit / Blocking
    // ------------------------------
    private void enforceLimit(String pkg) {
        if (pkg == null) return;

        var rule = policyStore.getPolicyForPackage(pkg);

        // If no rule → APP NOT ALLOWED → BLOCK
        if (rule == null) {
            dph.suspendPackage(pkg);
            return;
        }

        String studentId = DataStoreManager.getInstance(this).getUserIdValue();
        String date;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.now().toString();
        } else {
            date = null;
        }

        long usedMillis = appUsageDao.getDuration(studentId, date, pkg);
        long limitMillis = rule.getDailyLimitMinutes() * 60L * 1000L;

        if (usedMillis >= limitMillis && limitMillis > 0) {
            Log.d(TAG, "Time limit exceeded for " + pkg);

            dph.suspendPackage(pkg);

            executor.execute(() -> {
                AppUsage usage = appUsageDao.getByDateAndPackage(studentId, date, pkg);

                if (usage != null && usage.blockedAt == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        usage.blockedAt = Instant.now().toString();
                    }
                    appUsageDao.insertOrUpdate(usage);
                }
            });
        }
    }

    // ------------------------------
    // Cleanup
    // ------------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        executor.shutdown();
        Log.d(TAG, "Usage Monitor Service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
