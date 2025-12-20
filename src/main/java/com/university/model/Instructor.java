package com.university.model;

import java. time.LocalDateTime;

public class Instructor {
    private int instructorId;
    private String name;
    private String email;
    private String password;
    private String department;
    private LocalDateTime createdAt;

    public Instructor() {}

    public Instructor(int instructorId, String name, String email, String department) {
        this.instructorId = instructorId;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // Getters
    public int getInstructorId() {
        return instructorId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDepartment() {
        return department;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartment(String department) {
        this. department = department;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}