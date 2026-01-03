package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Instructor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    public int create(Instructor instructor) throws SQLException {
        String sql = "INSERT INTO instructors (instructor_number, first_name, last_name, email, password, title, department) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING instructor_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, instructor.getInstructorNumber());
            stmt.setString(2, instructor.getFirstName());
            stmt.setString(3, instructor.getLastName());
            stmt.setString(4, instructor.getEmail());
            stmt.setString(5, instructor.getPassword());
            stmt.setString(6, instructor.getTitle());
            stmt.setString(7, instructor.getDepartment());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("instructor_id");
            }
        }
        return -1;
    }

    public Instructor findById(int id) throws SQLException {
        String sql = "SELECT * FROM instructors WHERE instructor_id = ?";
        
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

    public Instructor findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM instructors WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    public List<Instructor> findAll() throws SQLException {
        String sql = "SELECT * FROM instructors ORDER BY instructor_id";
        List<Instructor> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public boolean update(Instructor instructor) throws SQLException {
        String sql = "UPDATE instructors SET first_name=?, last_name=?, email=?, title=?, department=? WHERE instructor_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, instructor.getFirstName());
            stmt.setString(2, instructor.getLastName());
            stmt.setString(3, instructor.getEmail());
            stmt.setString(4, instructor.getTitle());
            stmt.setString(5, instructor.getDepartment());
            stmt.setInt(6, instructor.getInstructorId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM instructors WHERE instructor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Instructor mapResultSet(ResultSet rs) throws SQLException {
        Instructor i = new Instructor();
        i.setInstructorId(rs.getInt("instructor_id"));
        i.setInstructorNumber(rs.getString("instructor_number"));
        i.setFirstName(rs.getString("first_name"));
        i.setLastName(rs.getString("last_name"));
        i.setEmail(rs.getString("email"));
        i.setPassword(rs.getString("password"));
        i.setTitle(rs.getString("title"));
        i.setDepartment(rs.getString("department"));
        i.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return i;
    }
}