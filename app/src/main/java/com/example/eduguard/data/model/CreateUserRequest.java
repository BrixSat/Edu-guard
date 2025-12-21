package com.example.eduguard.data.model;

public class CreateUserRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String assignedMentor; // can be null

    public CreateUserRequest(String name, String email, String phone,
                             String password, String role, String assignedMentor) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.assignedMentor = assignedMentor;
    }
}
