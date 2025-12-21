package com.example.eduguard.data.model;

import java.io.Serializable;

public class ExtraTimeRequest implements Serializable {

    private String _id; // MongoDB ID
    private String studentId;
    private String mentorId;

    private String type; // "extraTime" or "emergency"
    private String message;

    private String requestedForApp; // package name
    private Integer requestedExtraMinutes; // nullable

    private String status; // pending / approved / rejected

    private String createdAt;
    private String resolvedAt;

    public ExtraTimeRequest() {}

    // -------------------------
    // GETTERS & SETTERS
    // -------------------------
    public String getId() { return _id; }
    public void setId(String id) { this._id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getMentorId() { return mentorId; }
    public void setMentorId(String mentorId) { this.mentorId = mentorId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRequestedForApp() { return requestedForApp; }
    public void setRequestedForApp(String requestedForApp) { this.requestedForApp = requestedForApp; }

    public Integer getRequestedExtraMinutes() { return requestedExtraMinutes; }
    public void setRequestedExtraMinutes(Integer requestedExtraMinutes) {
        this.requestedExtraMinutes = requestedExtraMinutes;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(String resolvedAt) { this.resolvedAt = resolvedAt; }
}
