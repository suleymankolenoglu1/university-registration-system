package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Section;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDAO {

    public int create(Section section) throws SQLException {
        String sql = "INSERT INTO sections (course_id, instructor_id, room_id, semester, section_number, day_of_week, start_time, end_time, capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING section_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, section.getCourseId());
            stmt.setInt(2, section.getInstructorId());
            if (section.getRoomId() != null) {
                stmt.setInt(3, section.getRoomId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, section.getSemester());
            stmt.setInt(5, section.getSectionNumber());
            stmt.setString(6, section.getDayOfWeek());
            stmt.setTime(7, Time.valueOf(section.getStartTime()));
            stmt.setTime(8, Time.valueOf(section.getEndTime()));
            stmt.setInt(9, section.getCapacity());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("section_id");
            }
        }
        return -1;
    }

    public Section findById(int id) throws SQLException {
        String sql = "SELECT * FROM sections WHERE section_id = ?";
        
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

    public List<Section> findAll() throws SQLException {
        String sql = "SELECT * FROM sections ORDER BY section_id";
        List<Section> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Section> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT * FROM sections WHERE course_id = ?";
        List<Section> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Section> findByInstructor(int instructorId) throws SQLException {
        String sql = "SELECT * FROM sections WHERE instructor_id = ?";
        List<Section> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Section> findBySemester(String semester) throws SQLException {
        String sql = "SELECT * FROM sections WHERE semester = ?";
        List<Section> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, semester);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public boolean update(Section section) throws SQLException {
        String sql = "UPDATE sections SET instructor_id=?, room_id=?, day_of_week=?, start_time=?, end_time=?, capacity=? WHERE section_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, section.getInstructorId());
            if (section.getRoomId() != null) {
                stmt.setInt(2, section.getRoomId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, section.getDayOfWeek());
            stmt.setTime(4, Time.valueOf(section.getStartTime()));
            stmt.setTime(5, Time.valueOf(section.getEndTime()));
            stmt.setInt(6, section.getCapacity());
            stmt.setInt(7, section.getSectionId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM sections WHERE section_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Section mapResultSet(ResultSet rs) throws SQLException {
        Section s = new Section();
        s.setSectionId(rs.getInt("section_id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setInstructorId(rs.getInt("instructor_id"));
        int roomId = rs.getInt("room_id");
        s.setRoomId(rs.wasNull() ? null : roomId);
        s.setSemester(rs.getString("semester"));
        s.setSectionNumber(rs.getInt("section_number"));
        s.setDayOfWeek(rs.getString("day_of_week"));
        s.setStartTime(rs.getTime("start_time").toLocalTime());
        s.setEndTime(rs.getTime("end_time").toLocalTime());
        s.setCapacity(rs.getInt("capacity"));
        s.setEnrolledCount(rs.getInt("enrolled_count"));
        return s;
    }
}