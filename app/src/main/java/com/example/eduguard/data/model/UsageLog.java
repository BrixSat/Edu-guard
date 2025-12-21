package com.example.eduguard.data.model;

import java.util.List;

public class UsageLog {
    private String date;
    private List<AppUsage> appUsage;

    public String getDate() { return date; }
    public List<AppUsage> getAppUsage() { return appUsage; }

    public static class AppUsage {
        private String packageName;
        private int duration;

        public String getPackageName() { return packageName; }
        public int getDuration() { return duration; }
    }
}
