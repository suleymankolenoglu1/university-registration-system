package com.university.model;

import java.time.LocalDateTime;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private String description;
    private Integer prerequisiteCourseId;
    private String department;
    private LocalDateTime createdAt;
    
    // İlişkili obje (JOIN için)
    private Course prerequisiteCourse;

    public Course() {}

    // Getters
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public int getCredits() { return credits; }
    public String getDescription() { return description; }
    public Integer getPrerequisiteCourseId() { return prerequisiteCourseId; }
    public String getDepartment() { return department; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Course getPrerequisiteCourse() { return prerequisiteCourse; }

    // Setters
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setDescription(String description) { this.description = description; }
    public void setPrerequisiteCourseId(Integer prerequisiteCourseId) { this.prerequisiteCourseId = prerequisiteCourseId; }
    public void setDepartment(String department) { this.department = department; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setPrerequisiteCourse(Course prerequisiteCourse) { this.prerequisiteCourse = prerequisiteCourse; }

    public boolean hasPrerequisite() {
        return prerequisiteCourseId != null;
    }

    @Override
    public String toString() {
        return "Course{" + courseCode + " - " + courseName + ", " + credits + " kredi}";
    }
}