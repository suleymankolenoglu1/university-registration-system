package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public int create(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, description, prerequisite_course_id, department) VALUES (?, ?, ?, ?, ?, ?) RETURNING course_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDescription());
            if (course.getPrerequisiteCourseId() != null) {
                stmt.setInt(5, course.getPrerequisiteCourseId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setString(6, course.getDepartment());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("course_id");
            }
        }
        return -1;
    }

    public Course findById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_id = ?";
        
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

    public Course findByCode(String code) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSet(rs);
            }
        }
        return null;
    }

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT * FROM courses ORDER BY course_code";
        List<Course> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public List<Course> findByDepartment(String department) throws SQLException {
        String sql = "SELECT * FROM courses WHERE department = ? ORDER BY course_code";
        List<Course> list = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        }
        return list;
    }

    public boolean update(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_name=?, credits=?, description=?, prerequisite_course_id=?, department=? WHERE course_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, course.getCourseName());
            stmt.setInt(2, course.getCredits());
            stmt.setString(3, course.getDescription());
            if (course.getPrerequisiteCourseId() != null) {
                stmt.setInt(4, course.getPrerequisiteCourseId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, course.getDepartment());
            stmt.setInt(6, course.getCourseId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Course mapResultSet(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setCredits(rs.getInt("credits"));
        c.setDescription(rs.getString("description"));
        int prereq = rs.getInt("prerequisite_course_id");
        c.setPrerequisiteCourseId(rs.wasNull() ? null : prereq);
        c.setDepartment(rs.getString("department"));
        c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return c;
    }
}