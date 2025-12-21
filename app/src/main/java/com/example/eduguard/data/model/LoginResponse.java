package com.example.eduguard.data.model;

public class LoginResponse {
    private String token;
    private String role;
    private String userId;
    private String name;

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
}
