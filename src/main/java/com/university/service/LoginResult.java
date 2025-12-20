package com.university.service;

public class LoginResult {
    private boolean success;
    private String message;
    private Object user; // Student veya Instructor olabilir
    private UserType userType;

    public enum UserType {
        STUDENT,
        INSTRUCTOR
    }

    // Başarılı login için
    public static LoginResult success(Object user, UserType userType) {
        LoginResult result = new LoginResult();
        result.success = true;
        result.message = "Giriş başarılı";
        result.user = user;
        result.userType = userType;
        return result;
    }

    // Başarısız login için
    public static LoginResult failure(String message) {
        LoginResult result = new LoginResult();
        result.success = false;
        result.message = message;
        result.user = null;
        result.userType = null;
        return result;
    }

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getUser() {
        return user;
    }

    public UserType getUserType() {
        return userType;
    }
}