package com.university.model;

import java. time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private EnrollmentStatus status;
    private LocalDateTime enrollmentDate;
    private Integer waitlistPosition;

    public enum EnrollmentStatus {
        ENROLLED,
        WAITLIST,
        DROPPED
    }

    public Enrollment() {}

    // Getters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public Integer getWaitlistPosition() {
        return waitlistPosition;
    }

    // Setters
    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }
}