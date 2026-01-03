package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public int create(Room room) throws SQLException {
        String sql = "INSERT INTO rooms (room_code, building, capacity, room_type) VALUES (?, ?, ?, ?) RETURNING room_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomCode());
            stmt.setString(2, room.getBuilding());
            stmt.setInt(3, room.getCapacity());
            stmt.setString(4, room.getRoomType());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("room_id");
            }
        }
        return -1;
    }

    public Room findById(int id) throws SQLException {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    public List<Room> findAll() throws SQLException {
        String sql = "SELECT * FROM rooms ORDER BY room_code";
        List<Room> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    // Müsait derslikleri getir (stored procedure çağırır)
    public List<Room> findAvailable(String day, Time startTime, Time endTime, String semester) throws SQLException {
        String sql = "SELECT * FROM get_available_rooms(?, ?, ?, ?)";
        List<Room> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, day);
            stmt.setTime(2, startTime);
            stmt.setTime(3, endTime);
            stmt.setString(4, semester);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomCode(rs.getString("room_code"));
                r.setBuilding(rs.getString("building"));
                r.setCapacity(rs.getInt("capacity"));
                r.setRoomType(rs.getString("room_type"));
                list.add(r);
            }
        }
        return list;
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Room mapResultSet(ResultSet rs) throws SQLException {
        Room r = new Room();
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomCode(rs.getString("room_code"));
        r.setBuilding(rs.getString("building"));
        r.setCapacity(rs.getInt("capacity"));
        r.setRoomType(rs.getString("room_type"));
        return r;
    }
}