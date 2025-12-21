package com.example.eduguard;

import android.app.Application;

public class EduGuardApp extends Application {

    private static EduGuardApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static EduGuardApp getInstance() {
        return instance;
    }
}
