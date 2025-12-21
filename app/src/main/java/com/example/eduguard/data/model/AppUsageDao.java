package com.example.eduguard.data.model;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AppUsageDao {
    @Query("SELECT * FROM app_usage WHERE studentId = :studentId AND date = :date AND packageName = :pkg LIMIT 1")
    AppUsage getByDateAndPackage(String studentId, String date, String pkg);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertOrUpdate(AppUsage usage);

    @Query("UPDATE app_usage SET durationMillis = :duration WHERE studentId = :studentId AND date = :date AND packageName = :pkg")
    void updateDuration(String studentId, String date, String pkg, long duration);

    @Query("SELECT durationMillis FROM app_usage WHERE studentId = :studentId AND date = :date AND packageName = :pkg LIMIT 1")
    long getDuration(String studentId, String date, String pkg);
}
