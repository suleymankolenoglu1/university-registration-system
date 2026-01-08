package com.university.service;

import com.university.dao.WaitingListDAO;
import com.university.dao.EnrollmentDAO;
import com.university.model.WaitingList;
import com.university.model.Enrollment;
import java.sql.SQLException;

public class WaitlistService {
    
    private WaitingListDAO waitingListDAO;
    private EnrollmentDAO enrollmentDAO;
    
    public WaitlistService() {
        this.waitingListDAO = new WaitingListDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }
    
    public boolean addToWaitlist(int studentId, int sectionId) {
        try {
            int waitingId = waitingListDAO.add(studentId, sectionId);
            return waitingId > 0;
        } catch (SQLException e) {
            return false;
        }
    }
    
    public int getWaitlistPosition(int studentId, int sectionId) {
        try {
            return waitingListDAO.getPosition(studentId, sectionId);
        } catch (SQLException e) {
            return -1;
        }
    }
    
    public boolean removeFromWaitlist(int studentId, int sectionId) {
        try {
            return waitingListDAO.remove(studentId, sectionId);
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void promoteFromWaitlist(int sectionId) {
        // Trigger otomatik hallediyor
    }
}

