package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public int create(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?) RETURNING enrollment_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getSectionId());
            stmt.setString(3, enrollment.getStatus() != null ? enrollment.getStatus() : "ENROLLED");
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("enrollment_id");
            }
        }
        return -1;
    }

    public Enrollment findById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        
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

    public List<Enrollment> findByStudent(int studentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? ORDER BY enrollment_date DESC";
        List<Enrollment> list = new ArrayList<>();
        
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

    public List<Enrollment> findBySection(int sectionId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE section_id = ?";
        List<Enrollment> list = new ArrayList<>();
        
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

    public List<Enrollment> findActiveByStudent(int studentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND status = 'ENROLLED'";
        List<Enrollment> list = new ArrayList<>();
        
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

    // Ders bırakma
    public boolean dropCourse(int studentId, int sectionId) throws SQLException {
        String sql = "UPDATE enrollments SET status = 'DROPPED' WHERE student_id = ? AND section_id = ? AND status = 'ENROLLED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Not girişi
    public boolean updateGrade(int enrollmentId, String grade) throws SQLException {
        String sql = "UPDATE enrollments SET grade = ?, status = 'COMPLETED' WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, grade);
            stmt.setInt(2, enrollmentId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE enrollment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Enrollment mapResultSet(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setEnrollmentId(rs.getInt("enrollment_id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setSectionId(rs.getInt("section_id"));
        e.setEnrollmentDate(rs.getTimestamp("enrollment_date").toLocalDateTime());
        e.setStatus(rs.getString("status"));
        e.setGrade(rs.getString("grade"));
        return e;
    }
}