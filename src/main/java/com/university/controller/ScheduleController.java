package com.university.controller;

import com.university.App;
import com.university.model.*;
import com.university.service.*;
import com.university.dao.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.geometry.Pos;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Haftalık Program Controller
 */
public class ScheduleController implements Initializable {

    @FXML private Label studentNameLabel;
    @FXML private Label studentDeptLabel;
    @FXML private Label semesterInfoLabel;
    @FXML private GridPane scheduleGrid;
    
    private ScheduleService scheduleService;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;
    private RoomDAO roomDAO;
    
    private Student currentStudent;
    
    // Gün -> Sütun eşlemesi
    private static final Map<String, Integer> DAY_COLUMNS = new HashMap<>();
    static {
        DAY_COLUMNS.put("PAZARTESI", 1);
        DAY_COLUMNS.put("SALI", 2);
        DAY_COLUMNS.put("CARSAMBA", 3);
        DAY_COLUMNS.put("PERSEMBE", 4);
        DAY_COLUMNS.put("CUMA", 5);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scheduleService = new ScheduleService();
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
        roomDAO = new RoomDAO();
        
        currentStudent = LoginController.getCurrentStudent();
        
        if (currentStudent != null) {
            studentNameLabel.setText(currentStudent.getFullName());
            studentDeptLabel.setText(currentStudent.getDepartment());
            
            // Sınıf ve dönem bilgisini göster
            int sinif = (currentStudent.getSemester() + 1) / 2;
            String donem = (currentStudent.getSemester() % 2 == 1) ? "Güz" : "Bahar";
            if (semesterInfoLabel != null) {
                semesterInfoLabel.setText(sinif + ". Sınıf - " + donem + " Dönemi");
            }
        }
        
        // Boş hücreleri ekle
        initializeEmptyCells();
        
        // Dersleri yükle
        loadSchedule();
    }
    
    private void initializeEmptyCells() {
        // Tüm gün-saat kombinasyonları için boş hücreler
        for (int col = 1; col <= 5; col++) {
            for (int row = 1; row <= 8; row++) {
                if (row == 4) continue; // Öğle arası
                
                VBox emptyCell = new VBox();
                emptyCell.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: #bbb #ddd #bbb #ddd;" +
                    "-fx-border-width: 1 1 1 1;" +
                    "-fx-min-height: 55;"
                );
                emptyCell.setMaxWidth(Double.MAX_VALUE);
                emptyCell.setMaxHeight(Double.MAX_VALUE);
                GridPane.setHgrow(emptyCell, Priority.ALWAYS);
                GridPane.setVgrow(emptyCell, Priority.ALWAYS);
                scheduleGrid.add(emptyCell, col, row);
            }
            
            // Öğle arası
            VBox lunchCell = new VBox();
            lunchCell.setStyle(
                "-fx-background-color: #e8f5e9;" +
                "-fx-border-color: #888 #ddd #888 #ddd;" +
                "-fx-border-width: 2 1 2 1;"
            );
            lunchCell.setMaxWidth(Double.MAX_VALUE);
            lunchCell.setMaxHeight(Double.MAX_VALUE);
            scheduleGrid.add(lunchCell, col, 4);
        }
    }

    private void loadSchedule() {
        if (currentStudent == null) return;
        
        List<Section> schedule = scheduleService.getStudentSchedule(currentStudent.getStudentId());
        
        for (Section section : schedule) {
            try {
                Course course = courseDAO.findById(section.getCourseId());
                Instructor instructor = instructorDAO.findById(section.getInstructorId());
                Room room = section.getRoomId() != null ? roomDAO.findById(section.getRoomId()) : null;
                
                // Günü sütuna çevir
                Integer col = DAY_COLUMNS.get(section.getDayOfWeek());
                if (col == null) continue;
                
                // Saati satıra çevir
                int startRow = getRowForTime(section.getStartTime());
                int endRow = getRowForTime(section.getEndTime());
                int rowSpan = endRow - startRow;
                if (rowSpan <= 0) rowSpan = 1;
                
                // Her saat için ayrı kart oluştur (çizgilerle ayrılmış)
                VBox courseCard = createMultiHourCourseCard(
                    course != null ? course.getCourseCode() : "",
                    course != null ? course.getCourseName() : "",
                    instructor != null ? instructor.getFullName() : "",
                    room != null ? room.getRoomCode() : "TBA",
                    section.getStartTime(),
                    section.getEndTime(),
                    rowSpan
                );
                
                // Grid'e ekle
                scheduleGrid.add(courseCard, col, startRow, 1, rowSpan);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private int getRowForTime(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 9 && hour < 12) return hour - 8;  // 9->1, 10->2, 11->3
        if (hour == 12) return 4;  // Öğle
        if (hour >= 13 && hour <= 17) return hour - 8; // 13->5, 14->6, 15->7, 16->8
        return 1;
    }
    
    

/**
 * Çok saatlik dersler için saatler arası çizgili kart oluşturur
 */
private VBox createMultiHourCourseCard(String code, String name, String instructor, 
                                        String room, LocalTime startTime, LocalTime endTime, int hours) {
    VBox mainCard = new VBox(0);
    mainCard.setStyle(
        "-fx-background-color: #4CAF50;" +
        "-fx-background-radius: 5;" +
        "-fx-border-color: #388E3C;" +
        "-fx-border-width: 0 0 0 4;" +
        "-fx-border-radius: 5;"
    );
    mainCard.setMaxWidth(Double.MAX_VALUE);
    mainCard.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(mainCard, Priority.ALWAYS);
    
    for (int i = 0; i < hours; i++) {
        VBox hourCell = new VBox(2);
        hourCell.setAlignment(Pos.CENTER);
        hourCell.setStyle("-fx-padding: 6;");
        hourCell.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(hourCell, Priority.ALWAYS);
        
        // Her saatte ders kodu göster
        Label codeLabel = new Label(code);
        codeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        
        // Her saatte ders adı göster
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        
        // Oda bilgisi
        Label roomLabel = new Label("📍 " + room);
        roomLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: rgba(255,255,255,0.85);");
        
        hourCell.getChildren().addAll(codeLabel, nameLabel, roomLabel);
        
        mainCard.getChildren().add(hourCell);
        
        // Saatler arası beyaz çizgi ekle (son saat hariç)
        if (i < hours - 1) {
            HBox separator = new HBox();
            separator.setStyle(
                "-fx-background-color: rgba(255,255,255,0.6);" +
                "-fx-min-height: 2;" +
                "-fx-max-height: 2;"
            );
            separator.setMaxWidth(Double.MAX_VALUE);
            mainCard.getChildren().add(separator);
        }
    }
    
    return mainCard;
}

// ...existing code...
    
    @FXML
    private void goToDashboard() {
        navigateTo("/fxml/student-dashboard.fxml");
    }
    
    @FXML
    private void goToCourseSearch() {
        navigateTo("/fxml/course-search.fxml");
    }
    
    @FXML
    private void handleLogout() {
        LoginController.logout();
        navigateTo("/fxml/login.fxml");
    }
    
    private void navigateTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}