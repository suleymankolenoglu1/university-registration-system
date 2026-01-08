package com.university.service;

import com.university.model.Course;
import com.university.model.Section;
import com.university.model.Enrollment;
import com.university.dao.CourseDAO;
import com.university.dao.SectionDAO;
import com.university.dao.EnrollmentDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class CourseService {

    private CourseDAO courseDAO;
    private SectionDAO sectionDAO;
    private EnrollmentDAO enrollmentDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    /**
     * Tüm dersleri getir
     */
    public List<Course> getAllCourses() {
        try {
            return courseDAO.findAll();
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Bölüme göre dersleri getir
     */
    public List<Course> getCoursesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return courseDAO.findByDepartment(department.trim());
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Ders ara (isim veya kod ile)
     */
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCourses();
        }
        return getAllCourses(); // search metodu yok, findAll kullanıldı
    }

    /**
     * Dersi ID ile bul
     */
    public Course getCourseById(int courseId) {
        try {
            return courseDAO.findById(courseId);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Dersin tüm section'larını getir
     */
    public List<Section> getSectionsByCourse(int courseId) {
        try {
            return sectionDAO.findByCourse(courseId);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Açık (kontenjanı dolu olmayan) section'ları getir
     */
    public List<Section> getAvailableSections(int courseId) {
        List<Section> allSections = getSectionsByCourse(courseId);
        List<Section> availableSections = new ArrayList<>();
        
        for (Section section : allSections) {
            if (!section.isFull()) {
                availableSections.add(section);
            }
        }
        return availableSections;
    }

    /**
     * Dersin ön koşullarını getir
     */
    public Course getPrerequisite(int courseId) {
        try {
            Course course = courseDAO.findById(courseId);
            if (course == null || course.getPrerequisiteCourseId() == null) {
                return null;
            }
            return courseDAO.findById(course.getPrerequisiteCourseId());
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Öğrenci ön koşulu tamamlamış mı?
     */
    public boolean hasCompletedPrerequisite(int studentId, int courseId) {
        try {
            Course course = courseDAO.findById(courseId);
            
            // Ön koşul yoksa true döner
            if (course == null || course.getPrerequisiteCourseId() == null) {
                return true;
            }

            // Öğrenci ön koşul dersini tamamlamış mı? (enrollments tablosundan kontrol)
            List<Enrollment> enrollments = enrollmentDAO.findByStudent(studentId);
            for (Enrollment e : enrollments) {
                if ("COMPLETED".equals(e.getStatus())) {
                    Section section = sectionDAO.findById(e.getSectionId());
                    if (section != null && section.getCourseId() == course.getPrerequisiteCourseId()) {
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Öğrencinin tamamladığı dersleri getir
     */
    public List<Course> getCompletedCourses(int studentId) {
        List<Course> completedCourses = new ArrayList<>();
        try {
            List<Enrollment> enrollments = enrollmentDAO.findByStudent(studentId);
            for (Enrollment e : enrollments) {
                if ("COMPLETED".equals(e.getStatus())) {
                    Section section = sectionDAO.findById(e.getSectionId());
                    if (section != null) {
                        Course course = courseDAO.findById(section.getCourseId());
                        if (course != null) {
                            completedCourses.add(course);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            // Hata durumunda boş liste döner
        }
        return completedCourses;
    }

    /**
     * Öğrencinin alabileceği dersleri getir (ön koşulları tamamlanmış)
     */
    public List<Course> getEligibleCourses(int studentId) {
        List<Course> allCourses = getAllCourses();
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