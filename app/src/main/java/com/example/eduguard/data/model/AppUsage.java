package com.example.eduguard.data.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_usage",
        indices = {@Index(value = {"studentId","date","packageName"}, unique = true)})
public class AppUsage {
    @PrimaryKey(autoGenerate = true) public long id;
    public String studentId;           // use current student id
    public String date;                // "yyyy-MM-dd"
    public String packageName;
    public long durationMillis;        // total usage for that date
    public String blockedAt;           // ISO timestamp when blocked (optional)
}
