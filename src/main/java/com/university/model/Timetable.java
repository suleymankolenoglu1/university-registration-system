package com.university.model;

import java.time.LocalTime;

public class Timetable {
    private int timetableId;
    private int sectionId;
    private int roomId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public Timetable() {}

    // Getters
    public int getTimetableId() {
        return timetableId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Setters
    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(LocalTime startTime) {
        this. startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}