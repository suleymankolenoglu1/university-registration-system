package com.university.dao;

import com.university.config.DatabaseConnection;
import com.university.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Öğrenci ekle
    public int create(Student student) throws SQLException {
        String sql = "INSERT INTO students (student_number, first_name, last_name, email, password, department, semester) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING student_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getStudentNumber());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getPassword());
            stmt.setString(6, student.getDepartment());
            stmt.setInt(7, student.getSemester());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_id");
            }
        }
        return -1;
    }

    // ID ile öğrenci bul
    public Student findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    // Email ile öğrenci bul (login için)
    public Student findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM students WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    // Tüm öğrencileri getir
    public List<Student> findAll() throws SQLException {
        String sql = "SELECT * FROM students ORDER BY student_id";
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        }
        return students;
    }

    // Öğrenci güncelle
    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET first_name=?, last_name=?, email=?, department=?, semester=? WHERE student_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getDepartment());
            stmt.setInt(5, student.getSemester());
            stmt.setInt(6, student.getStudentId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    // Öğrenci sil
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ResultSet'i Student objesine çevir
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setStudentNumber(rs.getString("student_number"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPassword(rs.getString("password"));
        student.setDepartment(rs.getString("department"));
        student.setSemester(rs.getInt("semester"));
        student.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return student;
    }
}