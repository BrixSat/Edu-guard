package com.example.eduguard.mdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.eduguard.ui.sleep.SleepModeActivity;

public class SleepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {

        String action = intent.getAction();
        Log.d("SleepReceiver", "Received action: " + action);

        if ("START_SLEEP".equals(action)) {
            // Launch fullscreen lock
            Intent i = new Intent(ctx, SleepModeActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            ctx.startActivity(i);
        }
        else if ("END_SLEEP".equals(action)) {
            // Close the sleep UI if active
            SleepModeActivity.forceClose();
        }
    }
}
