package com.university.service;

import com.university.model.Section;
import com.university.model.Room;
import com.university.model.Enrollment;
import com.university.dao.SectionDAO;
import com.university.dao.RoomDAO;
import com.university.dao.EnrollmentDAO;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class ScheduleService {

    private SectionDAO sectionDAO;
    private RoomDAO roomDAO;
    private EnrollmentDAO enrollmentDAO;

    public ScheduleService() {
        this.sectionDAO = new SectionDAO();
        this.roomDAO = new RoomDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    // Constructor injection (test için)
    public ScheduleService(SectionDAO sectionDAO, RoomDAO roomDAO, EnrollmentDAO enrollmentDAO) {
        this.sectionDAO = sectionDAO;
        this.roomDAO = roomDAO;
        this.enrollmentDAO = enrollmentDAO;
    }

    /**
     * Öğrencinin mevcut programıyla yeni section çakışıyor mu?
     */
    public boolean hasTimeConflict(int studentId, int newSectionId) {
        try {
            // Yeni section bilgilerini al
            Section newSection = sectionDAO.findById(newSectionId);
            if (newSection == null) {
                return false;
            }

            // Öğrencinin kayıtlı olduğu section'ları al
            List<Section> studentSections = getStudentSchedule(studentId);

            // Her bir section ile çakışma kontrolü
            for (Section existingSection : studentSections) {
                if (isOverlapping(existingSection, newSection)) {
                    return true; // Çakışma var!
                }
            }

            return false; // Çakışma yok
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * İki section'ın zamanları çakışıyor mu?
     */
    public boolean isOverlapping(Section s1, Section s2) {
        // Farklı günlerdeyse çakışma yok
        if (!s1.getDayOfWeek().equalsIgnoreCase(s2.getDayOfWeek())) {
            return false;
        }

        // Aynı gündeler, saatleri kontrol et
        LocalTime start1 = s1.getStartTime();
        LocalTime end1 = s1.getEndTime();
        LocalTime start2 = s2.getStartTime();
        LocalTime end2 = s2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Öğrencinin haftalık ders programını getir
     */
    public List<Section> getStudentSchedule(int studentId) {
        List<Section> schedule = new ArrayList<>();
        try {
            List<Enrollment> enrollments = enrollmentDAO.findActiveByStudent(studentId);
            for (Enrollment e : enrollments) {
                Section section = sectionDAO.findById(e.getSectionId());
                if (section != null) {
                    schedule.add(section);
                }
            }
        } catch (SQLException e) {
            // Hata durumunda boş liste döner
        }
        return schedule;
    }

    /**
     * Öğretim elemanının haftalık programını getir
     */
    public List<Section> getInstructorSchedule(int instructorId) {
        try {
            return sectionDAO.findByInstructor(instructorId);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Belirli bir zaman diliminde boş derslikleri bul
     */
    public List<Room> findAvailableRooms(String dayOfWeek, LocalTime startTime, LocalTime endTime, String semester) {
        try {
            return roomDAO.findAvailable(dayOfWeek, 
                java.sql.Time.valueOf(startTime), 
                java.sql.Time.valueOf(endTime), 
                semester);
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Derslik belirli zamanda müsait mi?
     */
    public boolean isRoomAvailable(int roomId, String dayOfWeek, 
                                    LocalTime startTime, LocalTime endTime, String semester) {
        try {
            List<Section> sections = sectionDAO.findBySemester(semester);
            
            for (Section section : sections) {
                if (section.getRoomId() != null && section.getRoomId() == roomId) {
                    if (section.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                        // Zaman çakışması kontrolü
                        if (startTime.isBefore(section.getEndTime()) && 
                            endTime.isAfter(section.getStartTime())) {
                            return false; // Derslik dolu
                        }
                    }
                }
            }
            return true; // Derslik müsait
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Öğrenci için uygun section öner (çakışma olmayan)
     */
    public List<Section> suggestNonConflictingSections(int studentId, int courseId) {
        List<Section> availableSections = new ArrayList<>();
        try {
            List<Section> allSections = sectionDAO.findByCourse(courseId);

            for (Section section : allSections) {
                // Kontenjan dolu mu?
                if (section.isFull()) {
                    continue;
                }

                // Çakışma var mı?
                if (!hasTimeConflict(studentId, section.getSectionId())) {
                    availableSections.add(section);
                }
            }
        } catch (SQLException e) {
            // Hata durumunda boş liste döner
        }
        return availableSections;
    }

    /**
     * Öğrencinin programını güne göre getir
     */
    public List<Section> getScheduleByDay(int studentId, String dayOfWeek) {
        List<Section> fullSchedule = getStudentSchedule(studentId);
        List<Section> daySchedule = new ArrayList<>();

        for (Section s : fullSchedule) {
            if (s.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                daySchedule.add(s);
            }
        }

        // Saate göre sırala
        daySchedule.sort((s1, s2) -> s1.getStartTime().compareTo(s2.getStartTime()));

        return daySchedule;
    }

    /**
     * Programı formatla (görüntüleme için)
     */
    public String formatSchedule(List<Section> schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return "Program boş";
        }

        StringBuilder sb = new StringBuilder();
        String currentDay = "";

        // Güne göre grupla ve sırala
        schedule.sort((s1, s2) -> {
            int dayCompare = getDayOrder(s1.getDayOfWeek()) - getDayOrder(s2.getDayOfWeek());
            if (dayCompare != 0) return dayCompare;
            return s1.getStartTime().compareTo(s2.getStartTime());
        });

        for (Section s : schedule) {
            if (!s.getDayOfWeek().equals(currentDay)) {
                currentDay = s.getDayOfWeek();
                sb.append("\n").append(currentDay).append(":\n");
            }
            sb.append(String.format("  %s - %s\n", 
                s.getStartTime().toString(), 
                s.getEndTime().toString()));
        }

        return sb.toString();
    }

    /**
     * Gün sıralaması için yardımcı metod
     */
    private int getDayOrder(String day) {
        switch (day.toUpperCase()) {
            case "PAZARTESI": return 1;
            case "SALI": return 2;
            case "CARSAMBA": return 3;
            case "PERSEMBE": return 4;
            case "CUMA": return 5;
            case "CUMARTESI": return 6;
            case "PAZAR": return 7;
            default: return 8;
        }
    }
}
