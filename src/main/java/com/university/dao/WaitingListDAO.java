package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.WaitingList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WaitingListDAO {

    public int add(int studentId, int sectionId) throws SQLException {
        // Önce mevcut en yüksek pozisyonu bul
        String positionSql = "SELECT COALESCE(MAX(position), 0) + 1 FROM waiting_list WHERE section_id = ?";
        int newPosition = 1;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(positionSql)) {
            
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newPosition = rs.getInt(1);
            }
        }
        
        // Bekleme listesine ekle
        String sql = "INSERT INTO waiting_list (student_id, section_id, position) VALUES (?, ?, ?) RETURNING waiting_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setInt(3, newPosition);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("waiting_id");
            }
        }
        return -1;
    }

    public WaitingList findById(int id) throws SQLException {
        String sql = "SELECT * FROM waiting_list WHERE waiting_id = ?";
        
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

    public List<WaitingList> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT * FROM waiting_list WHERE section_id = ? ORDER BY position";
        List<WaitingList> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<WaitingList> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT * FROM waiting_list WHERE student_id = ?";
        List<WaitingList> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public int getPosition(int studentId, int sectionId) throws SQLException {
        String sql = "SELECT position FROM waiting_list WHERE student_id = ? AND section_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("position");
            }
        }
        return -1;
    }

    public boolean remove(int studentId, int sectionId) throws SQLException {
        String sql = "DELETE FROM waiting_list WHERE student_id = ? AND section_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        }
    }

    public int getWaitlistCount(int sectionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM waiting_list WHERE section_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private WaitingList mapResultSet(ResultSet rs) throws SQLException {
        WaitingList w = new WaitingList();
        w.setWaitingId(rs.getInt("waiting_id"));
        w.setStudentId(rs.getInt("student_id"));
        w.setSectionId(rs.getInt("section_id"));
        w.setPosition(rs.getInt("position"));
        w.setAddedDate(rs.getTimestamp("added_date").toLocalDateTime());
        return w;
    }
}