package com.university.service;

import com.university.model.Timetable;
import com.university.model.Section;
import com.university.model.Room;
import com.university.dao.TimetableDAO;
import com.university.dao.SectionDAO;
import com.university.dao.RoomDAO;
import com.university.dao.EnrollmentDAO;

import java.time. LocalTime;
import java. util.List;
import java.util. ArrayList;

public class ScheduleService {

    private TimetableDAO timetableDAO;
    private SectionDAO sectionDAO;
    private RoomDAO roomDAO;
    private EnrollmentDAO enrollmentDAO;

    public ScheduleService() {
        this.timetableDAO = new TimetableDAO();
        this.sectionDAO = new SectionDAO();
        this.roomDAO = new RoomDAO();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    // Constructor injection (test için)
    public ScheduleService(TimetableDAO timetableDAO, SectionDAO sectionDAO,
                           RoomDAO roomDAO, EnrollmentDAO enrollmentDAO) {
        this.timetableDAO = timetableDAO;
        this.sectionDAO = sectionDAO;
        this. roomDAO = roomDAO;
        this. enrollmentDAO = enrollmentDAO;
    }

    /**
     * Öğrencinin mevcut programıyla yeni section çakışıyor mu?
     */
    public boolean hasTimeConflict(int studentId, int newSectionId) {
        // Öğrencinin kayıtlı olduğu section'ların zaman bilgilerini al
        List<Timetable> studentSchedule = getStudentSchedule(studentId);
        
        // Yeni section'ın zaman bilgilerini al
        List<Timetable> newSectionTimes = timetableDAO.findBySectionId(newSectionId);

        // Her bir zaman dilimini kontrol et
        for (Timetable existingTime : studentSchedule) {
            for (Timetable newTime : newSectionTimes) {
                if (isOverlapping(existingTime, newTime)) {
                    return true; // Çakışma var! 
                }
            }
        }

        return false; // Çakışma yok
    }

    /**
     * İki zaman dilimi çakışıyor mu?
     */
    public boolean isOverlapping(Timetable t1, Timetable t2) {
        // Farklı günlerdeyse çakışma yok
        if (! t1.getDayOfWeek().equalsIgnoreCase(t2.getDayOfWeek())) {
            return false;
        }

        // Aynı gündeler, saatleri kontrol et
        // Çakışma durumu: t1.start < t2.end VE t2.start < t1.end
        LocalTime start1 = t1.getStartTime();
        LocalTime end1 = t1.getEndTime();
        LocalTime start2 = t2.getStartTime();
        LocalTime end2 = t2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    /**
     * Öğrencinin haftalık ders programını getir
     */
    public List<Timetable> getStudentSchedule(int studentId) {
        // Öğrencinin kayıtlı olduğu section ID'lerini al
        List<Integer> enrolledSectionIds = enrollmentDAO.getEnrolledSectionIds(studentId);

        List<Timetable> schedule = new ArrayList<>();

        // Her section'ın zaman bilgilerini topla
        for (Integer sectionId : enrolledSectionIds) {
            List<Timetable> sectionTimes = timetableDAO.findBySectionId(sectionId);
            schedule.addAll(sectionTimes);
        }

        return schedule;
    }

    /**
     * Öğretim elemanının haftalık programını getir
     */
    public List<Timetable> getInstructorSchedule(int instructorId) {
        // Öğretim elemanının verdiği section'ları bul
        List<Section> instructorSections = sectionDAO.findByInstructorId(instructorId);

        List<Timetable> schedule = new ArrayList<>();

        for (Section section : instructorSections) {
            List<Timetable> sectionTimes = timetableDAO.findBySectionId(section.getSectionId());
            schedule.addAll(sectionTimes);
        }

        return schedule;
    }

    /**
     * Section'ın ders programını getir
     */
    public List<Timetable> getSectionSchedule(int sectionId) {
        return timetableDAO. findBySectionId(sectionId);
    }

    /**
     * Belirli bir zaman diliminde boş derslikleri bul
     */
    public List<Room> findAvailableRooms(String dayOfWeek, LocalTime startTime, LocalTime endTime) {
        List<Room> allRooms = roomDAO.findAll();
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : allRooms) {
            if (isRoomAvailable(room. getRoomId(), dayOfWeek, startTime, endTime)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    /**
     * Derslik belirli zamanda müsait mi?
     */
    public boolean isRoomAvailable(int roomId, String dayOfWeek, 
                                    LocalTime startTime, LocalTime endTime) {
        List<Timetable> roomSchedule = timetableDAO.findByRoomId(roomId);

        // Geçici bir Timetable oluştur karşılaştırma için
        Timetable newTime = new Timetable();
        newTime.setDayOfWeek(dayOfWeek);
        newTime.setStartTime(startTime);
        newTime.setEndTime(endTime);

        for (Timetable existing : roomSchedule) {
            if (isOverlapping(existing, newTime)) {
                return false; // Derslik dolu
            }
        }

        return true; // Derslik müsait
    }

    /**
     * Öğrenci için uygun section öner (çakışma olmayan)
     */
    public List<Section> suggestNonConflictingSections(int studentId, int courseId) {
        List<Section> allSections = sectionDAO.findByCourseId(courseId);
        List<Section> availableSections = new ArrayList<>();

        for (Section section : allSections) {
            // Kontenjan dolu mu?
            if (section.isFull()) {
                continue;
            }

            // Çakışma var mı?
            if (! hasTimeConflict(studentId, section. getSectionId())) {
                availableSections.add(section);
            }
        }

        return availableSections;
    }

    /**
     * Öğrencinin programını güne göre getir
     */
    public List<Timetable> getScheduleByDay(int studentId, String dayOfWeek) {
        List<Timetable> fullSchedule = getStudentSchedule(studentId);
        List<Timetable> daySchedule = new ArrayList<>();

        for (Timetable t : fullSchedule) {
            if (t.getDayOfWeek().equalsIgnoreCase(dayOfWeek)) {
                daySchedule.add(t);
            }
        }

        // Saate göre sırala
        daySchedule.sort((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));

        return daySchedule;
    }

    /**
     * Programı formatla (görüntüleme için)
     */
    public String formatSchedule(List<Timetable> schedule) {
        if (schedule == null || schedule.isEmpty()) {
            return "Program boş";
        }

        StringBuilder sb = new StringBuilder();
        String currentDay = "";

        // Güne göre grupla
        schedule.sort((t1, t2) -> {
            int dayCompare = getDayOrder(t1.getDayOfWeek()) - getDayOrder(t2.getDayOfWeek());
            if (dayCompare != 0) return dayCompare;
            return t1.getStartTime().compareTo(t2.getStartTime());
        });

        for (Timetable t : schedule) {
            if (!t.getDayOfWeek().equals(currentDay)) {
                currentDay = t.getDayOfWeek();
                sb.append("\n").append(currentDay).append(":\n");
            }
            sb. append(String.format("  %s - %s\n", 
                t.getStartTime().toString(), 
                t.getEndTime().toString()));
        }

        return sb. toString();
    }

    /**
     * Gün sıralaması için yardımcı metod
     */
    private int getDayOrder(String day) {
        switch (day.toUpperCase()) {
            case "MONDAY":  return 1;
            case "TUESDAY": return 2;
            case "WEDNESDAY": return 3;
            case "THURSDAY":  return 4;
            case "FRIDAY": return 5;
            case "SATURDAY": return 6;
            case "SUNDAY": return 7;
            default: return 8;
        }
    }
}