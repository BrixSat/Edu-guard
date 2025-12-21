package com.example.eduguard.data.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    private String id;

    private String name;
    private String email;
    private String phone;
    private String password;  // Only used when creating a user

    private String role;      // admin / mentor / student

    @SerializedName("assignedMentor")
    private String assignedMentorId;  // stores mentorId returned from backend

    @SerializedName("status")
    private String status; // active / disabled

    // -----------------------------
    // GETTERS & SETTERS
    // -----------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getAssignedMentorId() { return assignedMentorId; }

    public void setAssignedMentor(String mentorId) {
        this.assignedMentorId = mentorId;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public char[] getAssignedMentor() {
        return new char[0];
    }
}
