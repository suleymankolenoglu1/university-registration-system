package com.university.model;

import java.time.LocalTime;

public class Section {
    private int sectionId;
    private int courseId;
    private int instructorId;
    private Integer roomId;
    private String semester;
    private int sectionNumber;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int enrolledCount;
    
    // İlişkili objeler
    private Course course;
    private Instructor instructor;
    private Room room;

    public Section() {}

    // Getters
    public int getSectionId() { return sectionId; }
    public int getCourseId() { return courseId; }
    public int getInstructorId() { return instructorId; }
    public Integer getRoomId() { return roomId; }
    public String getSemester() { return semester; }
    public int getSectionNumber() { return sectionNumber; }
    public String getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }
    public Course getCourse() { return course; }
    public Instructor getInstructor() { return instructor; }
    public Room getRoom() { return room; }

    // Setters
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setSectionNumber(int sectionNumber) { this.sectionNumber = sectionNumber; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }
    public void setCourse(Course course) { this.course = course; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }
    public void setRoom(Room room) { this.room = room; }

    public boolean isFull() {
        return enrolledCount >= capacity;
    }

    public int getAvailableSeats() {
        return capacity - enrolledCount;
    }

    @Override
    public String toString() {
        return "Section{" + sectionNumber + ", " + dayOfWeek + " " + startTime + "-" + endTime + "}";
    }
}