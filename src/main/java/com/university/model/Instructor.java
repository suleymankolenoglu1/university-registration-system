package com.university.model;

import java.time.LocalDateTime;

public class Instructor {
    private int instructorId;
    private String instructorNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String title;
    private String department;
    private LocalDateTime createdAt;

    public Instructor() {}

    // Getters
    public int getInstructorId() { return instructorId; }
    public String getInstructorNumber() { return instructorNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setInstructorNumber(String instructorNumber) { this.instructorNumber = instructorNumber; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setTitle(String title) { this.title = title; }
    public void setDepartment(String department) { this.department = department; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFullName() {
        return title + " " + firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Instructor{" + getFullName() + ", " + department + "}";
    }
}