package com.example.eduguard.mdm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WhitelistReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d("WhitelistReceiver", "Received: " + action);

        // Always enforce whitelist when triggered
//        WhitelistEnforcer enforcer = new WhitelistEnforcer(context);
//        enforcer.enforceWhitelist();
    }
}
