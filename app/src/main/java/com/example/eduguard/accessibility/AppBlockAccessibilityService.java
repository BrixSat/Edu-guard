package com.example.eduguard.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.example.eduguard.mdm.DevicePolicyHelper;
import com.example.eduguard.mdm.EduGuardDeviceAdminReceiver;
import com.example.eduguard.policy.PolicyStore;
import com.example.eduguard.ui.block.BlockedOverlayActivity;

public class AppBlockAccessibilityService extends AccessibilityService {

    private PolicyStore store;
    private DevicePolicyHelper dph;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        store = new PolicyStore(this);
        dph = new DevicePolicyHelper(this,
                new ComponentName(this, EduGuardDeviceAdminReceiver.class));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return;

        CharSequence pkgName = event.getPackageName();
        if (pkgName == null) return;

        String pkg = pkgName.toString();

        // If package is suspended → show UI
        if (dph != null) {
            // show block screen
            Intent i = new Intent(this, BlockedOverlayActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            i.putExtra(BlockedOverlayActivity.BLOCK_REASON, "Daily time limit exceeded");
            i.putExtra(BlockedOverlayActivity.BLOCK_APP_NAME, pkg);

            startActivity(i);
        }
    }

    @Override
    public void onInterrupt() {}
}
