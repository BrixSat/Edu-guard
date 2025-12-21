package com.example.eduguard.data.model;

public class Request {
    private String _id;
    private String studentId;
    private String mentorId;
    private String type;
    private String message;
    private String requestedForApp;
    private Integer requestedExtraMinutes;
    private String status;

    public String getId() { return _id; }
    public String getStudentId() { return studentId; }
    public String getMentorId() { return mentorId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getRequestedForApp() { return requestedForApp; }
    public Integer getRequestedExtraMinutes() { return requestedExtraMinutes; }
    public String getStatus() { return status; }
}
