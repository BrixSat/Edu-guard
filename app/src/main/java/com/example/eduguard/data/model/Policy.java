package com.example.eduguard.data.model;

import java.io.Serializable;
import java.util.List;

public class Policy implements Serializable {

    private String _id;
    private String studentId;
    private List<AppRule> allowedApps;
    private SleepMode sleepMode;
    private String theme;

    public Policy() {}

    // -------- GETTERS --------
    public String getId() { return _id; }

    public String getStudentId() { return studentId; }

    public List<AppRule> getAllowedApps() { return allowedApps; }

    public SleepMode getSleepMode() { return sleepMode; }

    public String getTheme() { return theme; }

    // -------- SETTERS --------
    public void setId(String id) { this._id = id; }

    public void setStudentId(String studentId) { this.studentId = studentId; }

    public void setAllowedApps(List<AppRule> allowedApps) { this.allowedApps = allowedApps; }

    public void setSleepMode(SleepMode sleepMode) { this.sleepMode = sleepMode; }

    public void setTheme(String theme) { this.theme = theme; }

    // ---------------------------------------------------------------------
    // ------------------------ INNER CLASS: AppRule ------------------------
    // ---------------------------------------------------------------------

    public static class AppRule implements Serializable {

        private String packageName;
        private int dailyLimitMinutes;
        public boolean blocked;

        public AppRule() {}

        // GETTERS
        public String getPackageName() { return packageName; }

        public int getDailyLimitMinutes() { return dailyLimitMinutes; }

        public boolean isBlocked() { return blocked; }

        // SETTERS
        public void setPackageName(String packageName) { this.packageName = packageName; }

        public void setDailyLimitMinutes(int dailyLimitMinutes) { this.dailyLimitMinutes = dailyLimitMinutes; }

        public void setBlocked(boolean blocked) { this.blocked = blocked; }
    }

    // ---------------------------------------------------------------------
    // ---------------------- INNER CLASS: SleepMode ------------------------
    // ---------------------------------------------------------------------

    public static class SleepMode implements Serializable {

        private boolean enabled;
        private String startTime;  // "22:00"
        private String endTime;    // "06:00"

        public SleepMode() {}

        // GETTERS
        public boolean isEnabled() { return enabled; }

        public String getStartTime() { return startTime; }

        public String getEndTime() { return endTime; }

        // SETTERS
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public void setStartTime(String startTime) { this.startTime = startTime; }

        public void setEndTime(String endTime) { this.endTime = endTime; }
    }
}
