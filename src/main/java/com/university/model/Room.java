package com.university.model;

public class Room {
    private int roomId;
    private String roomCode;
    private String building;
    private int capacity;
    private String roomType;

    public Room() {}

    // Getters
    public int getRoomId() { return roomId; }
    public String getRoomCode() { return roomCode; }
    public String getBuilding() { return building; }
    public int getCapacity() { return capacity; }
    public String getRoomType() { return roomType; }

    // Setters
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public void setBuilding(String building) { this.building = building; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    @Override
    public String toString() {
        return "Room{" + roomCode + ", " + building + ", " + roomType + ", kapasite=" + capacity + "}";
    }
}