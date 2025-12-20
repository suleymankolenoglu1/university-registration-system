package com.university.model;

public class Section {
    private int sectionId;
    private int courseId;
    private int instructorId;
    private int capacity;
    private int enrolledCount;
    private String semester;
    
    // İlişkili objeler (JOIN ile gelecek)
    private Course course;
    private Instructor instructor;

    public Section() {}

    // Getters
    public int getSectionId() {
        return sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public String getSemester() {
        return semester;
    }

    public Course getCourse() {
        return course;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    // Setters
    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public void setCourseId(int courseId) {
        this. courseId = courseId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public void setSemester(String semester) {
        this. semester = semester;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    // Kontenjan dolu mu? 
    public boolean isFull() {
        return enrolledCount >= capacity;
    }

    // Kalan kontenjan
    public int getAvailableSeats() {
        return capacity - enrolledCount;
    }
}