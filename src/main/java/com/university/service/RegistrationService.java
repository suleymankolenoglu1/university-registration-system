package com.university.service;

import com.university.model.Student;
import com.university.model.Section;
import com.university.model.Enrollment;
import com.university.dao.StudentDAO;
import com.university.dao.SectionDAO;
import com.university.dao.EnrollmentDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class RegistrationService {

    private StudentDAO studentDAO;
    private SectionDAO sectionDAO;
    private EnrollmentDAO enrollmentDAO;
    private CourseService courseService;
    private ScheduleService scheduleService;
    private WaitlistService waitlistService;

    public RegistrationService() {
        this.studentDAO = new StudentDAO();
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.courseService = new CourseService();
        this.scheduleService = new ScheduleService();
        this.waitlistService = new WaitlistService();
    }

    // Constructor injection (test için)
    public RegistrationService(StudentDAO studentDAO, SectionDAO sectionDAO,
                                EnrollmentDAO enrollmentDAO, CourseService courseService,
                                ScheduleService scheduleService, WaitlistService waitlistService) {
        this.studentDAO = studentDAO;
        this.sectionDAO = sectionDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.courseService = courseService;
        this.scheduleService = scheduleService;
        this.waitlistService = waitlistService;
    }

    /**
     * Öğrenciyi derse kaydet
     */
    public RegistrationResult enrollStudent(int studentId, int sectionId) {
        try {
            // 1. Öğrenci var mı? 
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                return RegistrationResult.STUDENT_NOT_FOUND;
            }

            // 2. Section var mı? 
            Section section = sectionDAO.findById(sectionId);
            if (section == null) {
                return RegistrationResult.INVALID_SECTION;
            }

            // 3. Zaten kayıtlı mı?
            if (isAlreadyEnrolled(studentId, sectionId)) {
                return RegistrationResult.ALREADY_ENROLLED;
            }

            // 4. Aynı dersin başka section'ına kayıtlı mı? 
            if (isEnrolledInCourse(studentId, section.getCourseId())) {
                return RegistrationResult.ALREADY_ENROLLED;
            }

            // 5. Ön koşul kontrolü
            if (!courseService.hasCompletedPrerequisite(studentId, section.getCourseId())) {
                return RegistrationResult.PREREQUISITE_NOT_MET;
            }

            // 6. Zaman çakışması kontrolü
            if (scheduleService.hasTimeConflict(studentId, sectionId)) {
                return RegistrationResult.TIME_CONFLICT;
            }

            // 7. Kontenjan kontrolü
            if (section.isFull()) {
                // Bekleme listesine ekle
                boolean addedToWaitlist = waitlistService.addToWaitlist(studentId, sectionId);
                if (addedToWaitlist) {
                    return RegistrationResult.ADDED_TO_WAITLIST;
                }
                return RegistrationResult.CAPACITY_FULL;
            }

            // 8. Kayıt işlemi
            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setSectionId(sectionId);
            enrollment.setStatus("ENROLLED");

            int enrollmentId = enrollmentDAO.create(enrollment);

            if (enrollmentId > 0) {
                // Trigger enrolled_count'ı otomatik artırır
                return RegistrationResult.SUCCESS;
            } else {
                return RegistrationResult.ERROR;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return RegistrationResult.ERROR;
        }
    }

    /**
     * Öğrenciyi dersten çıkar
     */
    public boolean dropCourse(int studentId, int sectionId) {
        try {
            // Kaydı DROPPED olarak işaretle
            boolean dropped = enrollmentDAO.dropCourse(studentId, sectionId);

            if (dropped) {
                // Trigger enrolled_count'ı otomatik azaltır ve bekleme listesini yönetir
                waitlistService.promoteFromWaitlist(sectionId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Öğrenci bu section'a kayıtlı mı?
     */
    public boolean isAlreadyEnrolled(int studentId, int sectionId) {
        try {
            List<Enrollment> enrollments = enrollmentDAO.findActiveByStudent(studentId);
            for (Enrollment e : enrollments) {
                if (e.getSectionId() == sectionId && "ENROLLED".equals(e.getStatus())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // Hata durumunda false döner
        }
        return false;
    }

    /**
     * Öğrenci bu dersin herhangi bir section'ına kayıtlı mı? 
     */
    public boolean isEnrolledInCourse(int studentId, int courseId) {
        try {
            List<Section> courseSections = sectionDAO.findByCourse(courseId);
            
            for (Section section : courseSections) {
                if (isAlreadyEnrolled(studentId, section.getSectionId())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            // Hata durumunda false döner
        }
        return false;
    }

    /**
     * Öğrencinin kayıtlı olduğu section'ları getir
     */
    public List<Section> getEnrolledSections(int studentId) {
        List<Section> sections = new ArrayList<>();
        try {
            List<Enrollment> enrollments = enrollmentDAO.findActiveByStudent(studentId);
            for (Enrollment e : enrollments) {
                Section section = sectionDAO.findById(e.getSectionId());
                if (section != null) {
                    sections.add(section);
                }
            }
        } catch (SQLException e) {
            // Hata durumunda boş liste döner
        }
        return sections;
    }

    /**
     * Öğrencinin toplam kredi sayısını hesapla
     */
    public int getTotalCredits(int studentId) {
        List<Section> enrolledSections = getEnrolledSections(studentId);
        int totalCredits = 0;

        for (Section section : enrolledSections) {
            if (section.getCourse() != null) {
                totalCredits += section.getCourse().getCredits();
            }
        }

        return totalCredits;
    }

    /**
     * Öğrenci maksimum kredi limitini aştı mı?
     */
    public boolean exceedsMaxCredits(int studentId, int sectionId, int maxCredits) {
        try {
            Section newSection = sectionDAO.findById(sectionId);
            if (newSection == null || newSection.getCourse() == null) {
                return false;
            }

            int currentCredits = getTotalCredits(studentId);
            int newCourseCredits = newSection.getCourse().getCredits();

            return (currentCredits + newCourseCredits) > maxCredits;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Kayıt durumunu getir
     */
    public String getEnrollmentStatus(int studentId, int sectionId) {
        try {
            List<Enrollment> enrollments = enrollmentDAO.findByStudent(studentId);
            Enrollment enrollment = null;
            
            for (Enrollment e : enrollments) {
                if (e.getSectionId() == sectionId) {
                    enrollment = e;
                    break;
                }
            }
            
            if (enrollment == null) {
                // Bekleme listesinde mi kontrol et
                int position = waitlistService.getWaitlistPosition(studentId, sectionId);
                if (position > 0) {
                    return "Bekleme listesinde (Sıra: " + position + ")";
                }
                return "Kayıtlı değil";
            }

            String status = enrollment.getStatus();
            if ("ENROLLED".equals(status)) {
                return "Kayıtlı";
            } else if ("DROPPED".equals(status)) {
                return "Dersten çekilmiş";
            } else if ("COMPLETED".equals(status)) {
                return "Tamamlandı";
            } else {
                return "Bilinmiyor";
            }
        } catch (SQLException e) {
            return "Hata";
        }
    }

    /**
     * Detaylı kayıt özeti
     */
    public String getEnrollmentSummary(int studentId) {
        List<Section> enrolledSections = getEnrolledSections(studentId);
        int totalCredits = getTotalCredits(studentId);

        StringBuilder sb = new StringBuilder();
        sb.append("===== KAYIT ÖZETİ =====\n");
        sb.append("Toplam Ders:  ").append(enrolledSections.size()).append("\n");
        sb.append("Toplam Kredi: ").append(totalCredits).append("\n");
        sb.append("\nDersler:\n");

        for (Section section : enrolledSections) {
            if (section.getCourse() != null) {
                sb.append("- ").append(section.getCourse().getCourseCode())
                  .append(": ").append(section.getCourse().getCourseName())
                  .append(" (").append(section.getCourse().getCredits()).append(" kredi)\n");
            }
        }

        return sb.toString();
    }
}