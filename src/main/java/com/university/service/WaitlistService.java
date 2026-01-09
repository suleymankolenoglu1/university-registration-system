package com.university.service;

import com.university.dao.WaitingListDAO;

import java.sql.SQLException;

public class WaitlistService {
    
    private WaitingListDAO waitingListDAO;
    
    public WaitlistService() {
        this.waitingListDAO = new WaitingListDAO();
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

