package com.example.eduguard.ui.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.example.eduguard.R;

public class SleepModeActivity extends Activity {

    private static Activity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;

        // Fullscreen lock
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        );

        setContentView(R.layout.activity_sleep_mode);
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        // Prevent closing
    }

    public static void forceClose() {
        if (instance != null) {
            instance.finish();
            instance = null;
        }
    }
}
