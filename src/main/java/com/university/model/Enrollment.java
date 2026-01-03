package com.university.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private LocalDateTime enrollmentDate;
    private String status; // ENROLLED, DROPPED, COMPLETED
    private String grade;
    
    // İlişkili objeler
    private Student student;
    private Section section;

    public Enrollment() {}

    // Getters
    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public LocalDateTime getEnrollmentDate() { return enrollmentDate; }
    public String getStatus() { return status; }
    public String getGrade() { return grade; }
    public Student getStudent() { return student; }
    public Section getSection() { return section; }

    // Setters
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setEnrollmentDate(LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setStatus(String status) { this.status = status; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setStudent(Student student) { this.student = student; }
    public void setSection(Section section) { this.section = section; }

    public boolean isEnrolled() {
        return "ENROLLED".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    @Override
    public String toString() {
        return "Enrollment{studentId=" + studentId + ", sectionId=" + sectionId + ", status=" + status + "}";
    }
}