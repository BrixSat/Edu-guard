package com.example.eduguard.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.eduguard.data.model.Policy;
import com.example.eduguard.mdm.EduGuardDeviceAdminReceiver;

import java.util.ArrayList;
import java.util.List;

public class WhitelistEnforcer {

    private final Context ctx;
    private final DevicePolicyManager dpm;
    private final ComponentName admin;

    public WhitelistEnforcer(Context context) {
        this.ctx = context.getApplicationContext();
        this.admin = new ComponentName(ctx, EduGuardDeviceAdminReceiver.class);
        this.dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    // ---------------------------------------------------------
    // MAIN METHOD → Apply whitelist policy to device
    // ---------------------------------------------------------
    public void apply(Policy policy) {
        if (policy == null || policy.getAllowedApps() == null) {
            Log.e("WhitelistEnforcer", "Policy or allowed apps list is null.");
            return;
        }

        List<String> allowedPackages = new ArrayList<>();

        // Always allow EduGuard
        allowedPackages.add(ctx.getPackageName());

        // Add apps allowed by admin
        policy.getAllowedApps().forEach(rule -> allowedPackages.add(rule.getPackageName()));

        Log.d("WhitelistEnforcer", "Allowed apps: " + allowedPackages);

        enforceAllowedApps(allowedPackages);
    }

    // ---------------------------------------------------------
    // Suspend all apps except allowed ones
    // ---------------------------------------------------------
    private void enforceAllowedApps(List<String> allowedPackages) {

        PackageManager pm = ctx.getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(0);

        for (ApplicationInfo app : installedApps) {
            String pkg = app.packageName;

            // Skip system apps
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            if (allowedPackages.contains(pkg)) {
                // UNSUSPEND allowed app
                try {
                    if (dpm.isPackageSuspended(admin, pkg)) {
                        dpm.setPackagesSuspended(admin, new String[]{pkg}, false);
                        Log.d("WhitelistEnforcer", "Unsuspended: " + pkg);
                    }
                } catch (Exception e) {
                    Log.e("WhitelistEnforcer", "Error unsuspending " + pkg + ": " + e.getMessage());
                }
            } else {
                // SUSPEND all other apps
                try {
                    dpm.setPackagesSuspended(admin, new String[]{pkg}, true);
                    Log.d("WhitelistEnforcer", "Suspended: " + pkg);
                } catch (Exception e) {
                    Log.e("WhitelistEnforcer", "Error suspending " + pkg + ": " + e.getMessage());
                }
            }
        }
    }
}
