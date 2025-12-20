package com.university.model;

public class Course {
    private int courseId;
    private String courseCode;
    private String name;
    private int credits;
    private String department;
    private Integer prerequisiteId; // null olabilir

    public Course() {}

    public Course(int courseId, String courseCode, String name, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
    }

    // Getters
    public int getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getName() {
        return name;
    }

    public int getCredits() {
        return credits;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getPrerequisiteId() {
        return prerequisiteId;
    }

    // Setters
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setPrerequisiteId(Integer prerequisiteId) {
        this.prerequisiteId = prerequisiteId;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", name='" + name + '\'' +
                ", credits=" + credits +
                '}';
    }
}