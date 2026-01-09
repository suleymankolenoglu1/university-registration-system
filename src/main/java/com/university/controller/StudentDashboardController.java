package com.university.controller;

import com.university.App;
import com.university.model.*;
import com.university.service.*;
import com.university.dao.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Öğrenci Dashboard Controller
 */
public class StudentDashboardController implements Initializable {

    // Sidebar
    @FXML private Label studentNameLabel;
    @FXML private Label studentDeptLabel;
    @FXML private Label studentNumberLabel;
    @FXML private Label studentInfoLabel;
    
    // Menü butonları
    @FXML private Button menuDashboard;
    @FXML private Button menuCourses;
    @FXML private Button menuSchedule;
    @FXML private Button menuEnrollments;
    @FXML private Button menuWaitlist;
    
    // İçerik
    @FXML private VBox contentArea;
    @FXML private Label pageTitle;
    @FXML private HBox statsBox;
    
    // İstatistikler
    @FXML private Label enrolledCountLabel;
    @FXML private Label totalCreditsLabel;
    @FXML private Label remainingCreditsLabel;
    @FXML private Label waitlistCountLabel;
    
    // Kayıtlı dersler tablosu
    @FXML private TableView<EnrolledCourseRow> enrolledCoursesTable;
    @FXML private TableColumn<EnrolledCourseRow, String> colCourseCode;
    @FXML private TableColumn<EnrolledCourseRow, String> colCourseName;
    @FXML private TableColumn<EnrolledCourseRow, String> colInstructor;
    @FXML private TableColumn<EnrolledCourseRow, String> colDay;
    @FXML private TableColumn<EnrolledCourseRow, String> colTime;
    @FXML private TableColumn<EnrolledCourseRow, String> colRoom;
    @FXML private TableColumn<EnrolledCourseRow, String> colCredits;
    @FXML private TableColumn<EnrolledCourseRow, Void> colAction;
    
    // Services
    private RegistrationService registrationService;

    
    // DAO
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;
    private RoomDAO roomDAO;
    private WaitingListDAO waitingListDAO;
    
    // Mevcut öğrenci
    private Student currentStudent;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Servisleri başlat
        registrationService = new RegistrationService();
        
        // DAO'ları başlat
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
        roomDAO = new RoomDAO();
        waitingListDAO = new WaitingListDAO();
        
        // Mevcut öğrenciyi al
        currentStudent = LoginController.getCurrentStudent();
        
        // Profil bilgilerini göster
        if (currentStudent != null) {
            studentNameLabel.setText("Hoş Geldiniz, " + currentStudent.getFullName());
            
            // Sınıf hesapla (dönem 1-2 = 1.sınıf, 3-4 = 2.sınıf, vb.)
            int sinif = (currentStudent.getSemester() + 1) / 2;
            String donem = (currentStudent.getSemester() % 2 == 1) ? "Güz" : "Bahar";
            String sinifBilgisi = sinif + ". Sınıf - " + donem + " Dönemi";
            
            // Header'da sağ üstte göster
            if (studentDeptLabel != null) {
                studentDeptLabel.setText(currentStudent.getDepartment());
            }
            if (studentNumberLabel != null) {
                studentNumberLabel.setText(currentStudent.getStudentNumber());
            }
            if (studentInfoLabel != null) {
                studentInfoLabel.setText(sinifBilgisi);
            }
        }
        
        // Tablo sütunlarını ayarla
        setupTableColumns();
        
        // Verileri yükle
        loadDashboardData();
    }
    
    /**
     * Tablo sütunlarını ayarla
     */
    private void setupTableColumns() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        
        // Ders bırakma butonu
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button dropBtn = new Button("Bırak");
            {
                dropBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                dropBtn.setOnAction(event -> {
                    EnrolledCourseRow row = getTableView().getItems().get(getIndex());
                    handleDropCourse(row);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : dropBtn);
            }
        });
    }
    
    /**
     * Dashboard verilerini yükle
     */
    private void loadDashboardData() {
        if (currentStudent == null) return;
        
        try {
            // Kayıtlı dersleri al
            List<Section> enrolledSections = registrationService.getEnrolledSections(currentStudent.getStudentId());
            
            // İstatistikleri güncelle
            enrolledCountLabel.setText(String.valueOf(enrolledSections.size()));
            
            int totalCredits = 0;
            ObservableList<EnrolledCourseRow> rows = FXCollections.observableArrayList();
            
            for (Section section : enrolledSections) {
                Course course = courseDAO.findById(section.getCourseId());
                Instructor instructor = instructorDAO.findById(section.getInstructorId());
                Room room = section.getRoomId() != null ? roomDAO.findById(section.getRoomId()) : null;
                
                if (course != null) {
                    totalCredits += course.getCredits();
                    
                    EnrolledCourseRow row = new EnrolledCourseRow(
                        section.getSectionId(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        instructor != null ? instructor.getFullName() : "Belirtilmemiş",
                        section.getDayOfWeek(),
                        section.getStartTime() + " - " + section.getEndTime(),
                        room != null ? room.getRoomCode() : "TBA",
                        String.valueOf(course.getCredits())
                    );
                    rows.add(row);
                }
            }
            
            totalCreditsLabel.setText(String.valueOf(totalCredits));
            enrolledCoursesTable.setItems(rows);
            
            // Kalan kredi (max 30 AKTS)
            int remainingCredits = RegistrationService.MAX_CREDITS_PER_SEMESTER - totalCredits;
            remainingCreditsLabel.setText(String.valueOf(remainingCredits));
            
            // Bekleme listesi sayısı
            List<WaitingList> waitlist = waitingListDAO.findByStudent(currentStudent.getStudentId());
            waitlistCountLabel.setText(String.valueOf(waitlist.size()));
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Veriler yüklenirken hata oluştu: " + e.getMessage());
        }
    }
    
    /**
     * Ders bırakma işlemi
     */
    private void handleDropCourse(EnrolledCourseRow row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ders Bırakma");
        confirm.setHeaderText(row.getCourseCode() + " - " + row.getCourseName());
        confirm.setContentText("Bu dersi bırakmak istediğinizden emin misiniz?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = registrationService.dropCourse(currentStudent.getStudentId(), row.getSectionId());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ders başarıyla bırakıldı.");
                loadDashboardData(); // Yenile
            } else {
                showAlert(Alert.AlertType.ERROR, "Hata", "Ders bırakılırken hata oluştu.");
            }
        }
    }
    
    // === MENÜ İŞLEMLERİ ===
    
    @FXML
    private void showDashboard() {
        setActiveMenu(menuDashboard);
        pageTitle.setText("📊 Ana Sayfa");
        loadDashboardData();
    }
    
    @FXML
    private void showCourseSearch() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/course-search.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Sayfa yüklenirken hata: " + e.getMessage());
        }
    }
    
    @FXML
    private void showSchedule() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/schedule.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Sayfa yüklenirken hata: " + e.getMessage());
        }
    }
    
    @FXML
    private void showEnrollments() {
        setActiveMenu(menuEnrollments);
        pageTitle.setText("✅ Kayıtlı Derslerim");
        // Dashboard'daki tablo zaten kayıtlı dersleri gösteriyor
    }
    
    @FXML
    private void showWaitlist() {
        setActiveMenu(menuWaitlist);
        pageTitle.setText("⏳ Bekleme Listem");
        loadWaitlistView();
    }
    
    private void loadWaitlistView() {
        // Bekleme listesi görünümü yükle
        try {
            List<WaitingList> waitlist = waitingListDAO.findByStudent(currentStudent.getStudentId());
            // Bekleme listesindeki ders sayısını güncelle
            waitlistCountLabel.setText(String.valueOf(waitlist.size()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Bekleme listesi yüklenemedi.");
        }
    }
    
    @FXML
    private void handleLogout() {
        LoginController.logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setTitle("🎓 Üniversite Ders Kayıt Sistemi");
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void setActiveMenu(Button activeButton) {
        menuDashboard.getStyleClass().remove("menu-button-active");
        menuCourses.getStyleClass().remove("menu-button-active");
        menuSchedule.getStyleClass().remove("menu-button-active");
        menuEnrollments.getStyleClass().remove("menu-button-active");
        menuWaitlist.getStyleClass().remove("menu-button-active");
        activeButton.getStyleClass().add("menu-button-active");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // === TABLO ROW SINIFI ===
    
    public static class EnrolledCourseRow {
        private int sectionId;
        private String courseCode;
        private String courseName;
        private String instructor;
        private String day;
        private String time;
        private String room;
        private String credits;
        
        public EnrolledCourseRow(int sectionId, String courseCode, String courseName, 
                                  String instructor, String day, String time, String room, String credits) {
            this.sectionId = sectionId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.instructor = instructor;
            this.day = day;
            this.time = time;
            this.room = room;
            this.credits = credits;
        }
        
        // Getters
        public int getSectionId() { return sectionId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getInstructor() { return instructor; }
        public String getDay() { return day; }
        public String getTime() { return time; }
        public String getRoom() { return room; }
        public String getCredits() { return credits; }
    }
}
