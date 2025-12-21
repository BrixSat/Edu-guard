package com.example.eduguard.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class DevicePolicyHelper {

    private final DevicePolicyManager dpm;
    private final ComponentName adminComponent;
    private final Context context;

    public DevicePolicyHelper(Context context, ComponentName adminComponent) {
        this.context = context.getApplicationContext();
        this.adminComponent = adminComponent;
        this.dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public boolean suspendPackage(String packageName) {
        try {
            dpm.setPackagesSuspended(adminComponent, new String[]{packageName}, true);
            Log.d("MDM", "Suspended: " + packageName);
            return true;
        } catch (Exception e) {
            Log.e("MDM", "Suspend error: ", e);
            return false;
        }
    }

    public boolean unsuspendPackage(String packageName) {
        try {
            dpm.setPackagesSuspended(adminComponent, new String[]{packageName}, false);
            Log.d("MDM", "Unsuspended: " + packageName);
            return true;
        } catch (Exception e) {
            Log.e("MDM", "Unsuspend error: ", e);
            return false;
        }
    }
}
