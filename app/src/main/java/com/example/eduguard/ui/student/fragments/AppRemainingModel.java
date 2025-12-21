package com.example.eduguard.ui.student.fragments;

import android.graphics.drawable.Drawable;

public class AppRemainingModel {

    public String packageName;
    public Drawable icon;
    public int used;
    public int limit;
    public int remaining;

    public AppRemainingModel(String pkg, Drawable icon, int used, int limit, int remaining) {
        this.packageName = pkg;
        this.icon = icon;
        this.used = used;
        this.limit = limit;
        this.remaining = remaining;
    }
}
