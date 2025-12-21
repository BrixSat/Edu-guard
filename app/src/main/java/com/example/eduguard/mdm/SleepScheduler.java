package com.example.eduguard.mdm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.eduguard.data.model.Policy;

import java.util.Calendar;

public class SleepScheduler {

    private final Context ctx;

    public SleepScheduler(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    // ------------------------------------------------------------------
    // APPLY SLEEP MODE FROM POLICY
    // ------------------------------------------------------------------
    public void apply(Policy.SleepMode sleepMode) {

        if (sleepMode == null || !sleepMode.isEnabled()) {
            cancelAlarms();
            return;
        }

        Log.d("SleepScheduler", "Applying sleep mode: " +
                sleepMode.getStartTime() + " → " + sleepMode.getEndTime());

        scheduleSleepStart(sleepMode.getStartTime());
        scheduleSleepEnd(sleepMode.getEndTime());
    }

    // ------------------------------------------------------------------
    // SCHEDULE START SLEEP
    // ------------------------------------------------------------------
    private void scheduleSleepStart(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        // If time already passed today → schedule for next day
        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent i = new Intent(ctx, SleepReceiver.class);
        i.setAction("START_SLEEP");

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, 1001, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Log.d("SleepScheduler", "Sleep start scheduled at: " + c.getTime());
    }

    // ------------------------------------------------------------------
    // SCHEDULE END SLEEP
    // ------------------------------------------------------------------
    private void scheduleSleepEnd(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent i = new Intent(ctx, SleepReceiver.class);
        i.setAction("END_SLEEP");

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx, 1002, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);

        Log.d("SleepScheduler", "Sleep end scheduled at: " + c.getTime());
    }

    // ------------------------------------------------------------------
    // CANCEL ANY OLD ALARMS
    // ------------------------------------------------------------------
    private void cancelAlarms() {
        Intent i1 = new Intent(ctx, SleepReceiver.class);
        PendingIntent pi1 = PendingIntent.getBroadcast(ctx, 1001, i1,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pi1 != null) pi1.cancel();

        Intent i2 = new Intent(ctx, SleepReceiver.class);
        PendingIntent pi2 = PendingIntent.getBroadcast(ctx, 1002, i2,
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);

        if (pi2 != null) pi2.cancel();

        Log.d("SleepScheduler", "Cancelled all sleep alarms.");
    }
}
