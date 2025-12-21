package com.example.eduguard.ui.block;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.eduguard.R;

public class BlockedOverlayActivity extends Activity {

    public static String BLOCK_REASON = "reason";
    public static String BLOCK_APP_NAME = "appName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen overlay blocking UI
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        setContentView(R.layout.activity_blocked_overlay);

        // Get block info from intent
        String reason = getIntent().getStringExtra(BLOCK_REASON);
        String appName = getIntent().getStringExtra(BLOCK_APP_NAME);

        TextView title = findViewById(R.id.blockTitle);
        TextView message = findViewById(R.id.blockMessage);

        title.setText("Access Blocked");
        message.setText(appName + " is blocked.\nReason: " + reason);
    }

    @Override
    public void onBackPressed() {
        // Prevent back button escape
    }
}
