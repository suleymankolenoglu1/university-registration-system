package com.university.model;

import java.time.LocalDateTime;

public class WaitingList {
    private int waitingId;
    private int studentId;
    private int sectionId;
    private int position;
    private LocalDateTime addedDate;
    
    // İlişkili objeler
    private Student student;
    private Section section;

    public WaitingList() {}

    // Getters
    public int getWaitingId() { return waitingId; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public int getPosition() { return position; }
    public LocalDateTime getAddedDate() { return addedDate; }
    public Student getStudent() { return student; }
    public Section getSection() { return section; }

    // Setters
    public void setWaitingId(int waitingId) { this.waitingId = waitingId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setPosition(int position) { this.position = position; }
    public void setAddedDate(LocalDateTime addedDate) { this.addedDate = addedDate; }
    public void setStudent(Student student) { this.student = student; }
    public void setSection(Section section) { this.section = section; }

    @Override
    public String toString() {
        return "WaitingList{studentId=" + studentId + ", sectionId=" + sectionId + ", position=" + position + "}";
    }
}