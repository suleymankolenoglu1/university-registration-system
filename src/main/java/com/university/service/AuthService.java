package com.university.service;

import com.university.model.Student;
import com. university.model. Instructor;
import com.university.dao.StudentDAO;
import com.university. dao.InstructorDAO;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private StudentDAO studentDAO;
    private InstructorDAO instructorDAO;

    public AuthService() {
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
    }

    // Constructor injection (test için kullanışlı)
    public AuthService(StudentDAO studentDAO, InstructorDAO instructorDAO) {
        this.studentDAO = studentDAO;
        this.instructorDAO = instructorDAO;
    }

    /**
     * Öğrenci girişi
     */
    public LoginResult loginStudent(String email, String password) {
        // Boş kontrol
        if (email == null || email.trim().isEmpty()) {
            return LoginResult.failure("Email adresi boş olamaz");
        }
        if (password == null || password.trim().isEmpty()) {
            return LoginResult.failure("Şifre boş olamaz");
        }

        // Öğrenciyi bul
        Student student = studentDAO.findByEmail(email. trim().toLowerCase());

        if (student == null) {
            return LoginResult.failure("Bu email ile kayıtlı öğrenci bulunamadı");
        }

        // Şifre kontrolü
        if (! verifyPassword(password, student.getPassword())) {
            return LoginResult.failure("Şifre hatalı");
        }

        return LoginResult.success(student, LoginResult.UserType. STUDENT);
    }

    /**
     * Öğretim elemanı girişi
     */
    public LoginResult loginInstructor(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            return LoginResult.failure("Email adresi boş olamaz");
        }
        if (password == null || password.trim().isEmpty()) {
            return LoginResult.failure("Şifre boş olamaz");
        }

        Instructor instructor = instructorDAO.findByEmail(email. trim().toLowerCase());

        if (instructor == null) {
            return LoginResult.failure("Bu email ile kayıtlı öğretim elemanı bulunamadı");
        }

        if (!verifyPassword(password, instructor.getPassword())) {
            return LoginResult.failure("Şifre hatalı");
        }

        return LoginResult.success(instructor, LoginResult.UserType.INSTRUCTOR);
    }

    /**
     * Yeni öğrenci kaydı
     */
    public LoginResult registerStudent(String name, String email, String password, 
                                        String department, int enrollmentYear) {
        // Validasyonlar
        if (name == null || name.trim().isEmpty()) {
            return LoginResult.failure("İsim boş olamaz");
        }
        if (email == null || email.trim().isEmpty()) {
            return LoginResult.failure("Email boş olamaz");
        }
        if (! isValidEmail(email)) {
            return LoginResult.failure("Geçersiz email formatı");
        }
        if (password == null || password. length() < 6) {
            return LoginResult.failure("Şifre en az 6 karakter olmalı");
        }

        // Email kullanılıyor mu?
        if (studentDAO.findByEmail(email. trim().toLowerCase()) != null) {
            return LoginResult.failure("Bu email adresi zaten kullanılıyor");
        }

        // Yeni öğrenci oluştur
        Student student = new Student();
        student.setName(name.trim());
        student.setEmail(email.trim().toLowerCase());
        student.setPassword(hashPassword(password));
        student.setDepartment(department);
        student.setEnrollmentYear(enrollmentYear);

        // Kaydet
        boolean saved = studentDAO.save(student);

        if (saved) {
            return LoginResult.success(student, LoginResult.UserType.STUDENT);
        } else {
            return LoginResult.failure("Kayıt sırasında bir hata oluştu");
        }
    }

    /**
     * Yeni öğretim elemanı kaydı
     */
    public LoginResult registerInstructor(String name, String email, 
                                           String password, String department) {
        if (name == null || name.trim().isEmpty()) {
            return LoginResult.failure("İsim boş olamaz");
        }
        if (email == null || email.trim().isEmpty()) {
            return LoginResult.failure("Email boş olamaz");
        }
        if (!isValidEmail(email)) {
            return LoginResult.failure("Geçersiz email formatı");
        }
        if (password == null || password.length() < 6) {
            return LoginResult.failure("Şifre en az 6 karakter olmalı");
        }

        if (instructorDAO. findByEmail(email.trim().toLowerCase()) != null) {
            return LoginResult. failure("Bu email adresi zaten kullanılıyor");
        }

        Instructor instructor = new Instructor();
        instructor.setName(name.trim());
        instructor.setEmail(email.trim().toLowerCase());
        instructor. setPassword(hashPassword(password));
        instructor.setDepartment(department);

        boolean saved = instructorDAO.save(instructor);

        if (saved) {
            return LoginResult. success(instructor, LoginResult.UserType. INSTRUCTOR);
        } else {
            return LoginResult.failure("Kayıt sırasında bir hata oluştu");
        }
    }

    /**
     * Şifre değiştirme
     */
    public boolean changePassword(int userId, String oldPassword, 
                                   String newPassword, boolean isStudent) {
        if (newPassword == null || newPassword.length() < 6) {
            return false;
        }

        if (isStudent) {
            Student student = studentDAO.findById(userId);
            if (student == null) {
                return false;
            }
            if (!verifyPassword(oldPassword, student. getPassword())) {
                return false;
            }
            return studentDAO.updatePassword(userId, hashPassword(newPassword));
        } else {
            Instructor instructor = instructorDAO. findById(userId);
            if (instructor == null) {
                return false;
            }
            if (!verifyPassword(oldPassword, instructor. getPassword())) {
                return false;
            }
            return instructorDAO. updatePassword(userId, hashPassword(newPassword));
        }
    }

    /**
     * Şifreyi hashle (BCrypt kullanarak)
     */
    public String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Şifre doğrulama
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Email formatı kontrolü
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(. +)$";
        return email. matches(emailRegex);
    }
}