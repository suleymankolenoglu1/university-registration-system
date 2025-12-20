package com. university.service;

import com.university. model.Course;
import com.university. model.Section;
import com. university.dao.CourseDAO;
import com.university.dao.SectionDAO;
import com.university.dao. CompletedCourseDAO;

import java.util.List;
import java. util.ArrayList;

public class CourseService {

    private CourseDAO courseDAO;
    private SectionDAO sectionDAO;
    private CompletedCourseDAO completedCourseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO();
        this.completedCourseDAO = new CompletedCourseDAO();
    }

    /**
     * Tüm dersleri getir
     */
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    /**
     * Bölüme göre dersleri getir
     */
    public List<Course> getCoursesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return courseDAO.findByDepartment(department. trim());
    }

    /**
     * Ders ara (isim veya kod ile)
     */
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCourses();
        }
        return courseDAO.search(keyword. trim());
    }

    /**
     * Dersi ID ile bul
     */
    public Course getCourseById(int courseId) {
        return courseDAO.findById(courseId);
    }

    /**
     * Dersin tüm section'larını getir
     */
    public List<Section> getSectionsByCourse(int courseId) {
        return sectionDAO. findByCourseId(courseId);
    }

    /**
     * Açık (kontenjanı dolu olmayan) section'ları getir
     */
    public List<Section> getAvailableSections(int courseId) {
        List<Section> allSections = sectionDAO.findByCourseId(courseId);
        List<Section> availableSections = new ArrayList<>();
        
        for (Section section : allSections) {
            if (! section.isFull()) {
                availableSections.add(section);
            }
        }
        return availableSections;
    }

    /**
     * Dersin ön koşullarını getir
     */
    public Course getPrerequisite(int courseId) {
        Course course = courseDAO.findById(courseId);
        if (course == null || course.getPrerequisiteId() == null) {
            return null;
        }
        return courseDAO.findById(course.getPrerequisiteId());
    }

    /**
     * Öğrenci ön koşulu tamamlamış mı?
     */
    public boolean hasCompletedPrerequisite(int studentId, int courseId) {
        Course course = courseDAO.findById(courseId);
        
        // Ön koşul yoksa true döner
        if (course == null || course.getPrerequisiteId() == null) {
            return true;
        }

        // Öğrenci ön koşul dersini tamamlamış mı?
        return completedCourseDAO.hasCompleted(studentId, course.getPrerequisiteId());
    }

    /**
     * Öğrencinin tamamladığı dersleri getir
     */
    public List<Course> getCompletedCourses(int studentId) {
        return completedCourseDAO.getCompletedCourses(studentId);
    }

    /**
     * Öğrencinin alabileceği dersleri getir (ön koşulları tamamlanmış)
     */
    public List<Course> getEligibleCourses(int studentId) {
        List<Course> allCourses = courseDAO.findAll();
        List<Course> eligibleCourses = new ArrayList<>();
        List<Course> completedCourses = getCompletedCourses(studentId);
        
        for (Course course : allCourses) {
            // Zaten tamamladıysa ekleme
            boolean alreadyCompleted = completedCourses.stream()
                .anyMatch(c -> c.getCourseId() == course.getCourseId());
            
            if (alreadyCompleted) {
                continue;
            }
            
            // Ön koşulu tamamladıysa ekle
            if (hasCompletedPrerequisite(studentId, course.getCourseId())) {
                eligibleCourses.add(course);
            }
        }
        
        return eligibleCourses;
    }
}