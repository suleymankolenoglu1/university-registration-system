package com.university.model;

import java.time.LocalDateTime;

public class Student {
    private int studentId;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String department;
    private int semester;
    private LocalDateTime createdAt;

    public Student() {}

    // Getters
    public int getStudentId() { return studentId; }
    public String getStudentNumber() { return studentNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDepartment() { return department; }
    public int getSemester() { return semester; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDepartment(String department) { this.department = department; }
    public void setSemester(int semester) { this.semester = semester; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Tam ad
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Student{" + studentNumber + ", " + getFullName() + ", " + department + "}";
    }
}