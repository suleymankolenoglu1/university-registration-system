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

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Ders Arama ve Kayıt Controller
 */
public class CourseSearchController implements Initializable {

    @FXML private Label studentNameLabel;
    @FXML private Label studentDeptLabel;
    @FXML private Label semesterInfoLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilter;
    @FXML private ComboBox<String> yearFilter;
    @FXML private ComboBox<String> dayFilter;
    @FXML private Label resultCountLabel;
    
    @FXML private TableView<CourseRow> coursesTable;
    @FXML private TableColumn<CourseRow, String> colCode;
    @FXML private TableColumn<CourseRow, String> colName;
    @FXML private TableColumn<CourseRow, String> colSection;
    @FXML private TableColumn<CourseRow, String> colInstructor;
    @FXML private TableColumn<CourseRow, String> colDay;
    @FXML private TableColumn<CourseRow, String> colTime;
    @FXML private TableColumn<CourseRow, String> colRoom;
    @FXML private TableColumn<CourseRow, String> colCapacity;
    @FXML private TableColumn<CourseRow, String> colCredits;
    @FXML private TableColumn<CourseRow, String> colPrereq;
    @FXML private TableColumn<CourseRow, Void> colEnroll;
    
    private RegistrationService registrationService;
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;
    private RoomDAO roomDAO;
    
    private Student currentStudent;
    private ObservableList<CourseRow> allCourses = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registrationService = new RegistrationService();
        sectionDAO = new SectionDAO();
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
        
        setupFilters();
        setupTableColumns();
        loadCourses();
    }
    
    private void setupFilters() {
        // Gün filtresi
        dayFilter.setItems(FXCollections.observableArrayList(
            "Tümü", "PAZARTESI", "SALI", "CARSAMBA", "PERSEMBE", "CUMA"
        ));
        dayFilter.setValue("Tümü");
        
        // Sınıf filtresi
        yearFilter.setItems(FXCollections.observableArrayList(
            "Tümü", "1. Sınıf", "2. Sınıf", "3. Sınıf", "4. Sınıf"
        ));
        yearFilter.setValue("Tümü");
        
        // Bölüm filtresi (arka planda, öğrencinin bölümüne sabit)
        if (departmentFilter != null) {
            departmentFilter.setValue(currentStudent != null ? currentStudent.getDepartment() : "Tümü");
        }
    }
    
    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colSection.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        colInstructor.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colPrereq.setCellValueFactory(new PropertyValueFactory<>("prerequisite"));
        
        // Kayıt ol butonu
        colEnroll.setCellFactory(param -> new TableCell<>() {
            private final Button enrollBtn = new Button("Kayıt Ol");
            {
                enrollBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
                enrollBtn.setOnAction(event -> {
                    CourseRow row = getTableView().getItems().get(getIndex());
                    handleEnroll(row);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CourseRow row = getTableView().getItems().get(getIndex());
                    if (row.isFull()) {
                        enrollBtn.setText("Bekleme");
                        enrollBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
                        enrollBtn.setDisable(false);
                    } else if (row.isEnrolled()) {
                        enrollBtn.setText("Kayıtlı ✓");
                        enrollBtn.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 5;");
                        enrollBtn.setDisable(true);
                    } else {
                        enrollBtn.setText("Kayıt Ol");
                        enrollBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
                        enrollBtn.setDisable(false);
                    }
                    setGraphic(enrollBtn);
                }
            }
        });
    }
    
    private void loadCourses() {
        allCourses.clear();
        
        try {
            List<Section> sections = sectionDAO.findBySemester("2024-2025-GUZ");
            
            for (Section section : sections) {
                Course course = courseDAO.findById(section.getCourseId());
                Instructor instructor = instructorDAO.findById(section.getInstructorId());
                Room room = section.getRoomId() != null ? roomDAO.findById(section.getRoomId()) : null;
                
                // Ön koşul kontrolü
                String prereq = "-";
                if (course != null && course.getPrerequisiteCourseId() != null) {
                    Course prereqCourse = courseDAO.findById(course.getPrerequisiteCourseId());
                    if (prereqCourse != null) {
                        prereq = prereqCourse.getCourseCode();
                    }
                }
                
                // Kayıtlı mı kontrolü
                boolean isEnrolled = registrationService.isAlreadyEnrolled(
                    currentStudent.getStudentId(), section.getSectionId());
                
                String capacityStr = section.getEnrolledCount() + "/" + section.getCapacity();
                
                CourseRow row = new CourseRow(
                    section.getSectionId(),
                    course != null ? course.getCourseCode() : "",
                    course != null ? course.getCourseName() : "",
                    String.valueOf(section.getSectionNumber()),
                    instructor != null ? instructor.getFullName() : "",
                    section.getDayOfWeek(),
                    section.getStartTime() + "-" + section.getEndTime(),
                    room != null ? room.getRoomCode() : "TBA",
                    capacityStr,
                    course != null ? String.valueOf(course.getCredits()) : "",
                    prereq,
                    section.isFull(),
                    isEnrolled,
                    course != null ? course.getDepartment() : ""
                );
                allCourses.add(row);
            }
            
            coursesTable.setItems(allCourses);
            resultCountLabel.setText(allCourses.size() + " ders bulundu");
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Dersler yüklenirken hata: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase().trim();
        String year = yearFilter.getValue();
        String day = dayFilter.getValue();
        
        ObservableList<CourseRow> filtered = allCourses.filtered(row -> {
            boolean matchesKeyword = keyword.isEmpty() || 
                row.getCourseCode().toLowerCase().contains(keyword) ||
                row.getCourseName().toLowerCase().contains(keyword);
            
            // Sınıf filtresi - ders kodundaki ilk rakama göre (YZM1xx=1, YZM2xx=2, vb.)
            boolean matchesYear = "Tümü".equals(year);
            if (!matchesYear && row.getCourseCode().length() >= 4) {
                char yearChar = row.getCourseCode().charAt(3);
                if (Character.isDigit(yearChar)) {
                    int courseYear = Character.getNumericValue(yearChar);
                    matchesYear = year.startsWith(String.valueOf(courseYear));
                }
            }
            
            boolean matchesDay = "Tümü".equals(day) || row.getDay().equals(day);
            
            return matchesKeyword && matchesYear && matchesDay;
        });
        
        coursesTable.setItems(filtered);
        resultCountLabel.setText(filtered.size() + " ders bulundu");
    }
    
    @FXML
    private void clearFilters() {
        searchField.clear();
        yearFilter.setValue("Tümü");
        dayFilter.setValue("Tümü");
        coursesTable.setItems(allCourses);
        resultCountLabel.setText(allCourses.size() + " ders bulundu");
    }
    
    private void handleEnroll(CourseRow row) {
        RegistrationResult result = registrationService.enrollStudent(
            currentStudent.getStudentId(), row.getSectionId());
        
        switch (result) {
            case SUCCESS:
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Kayıt Başarılı! ✅");
                successAlert.setHeaderText("Derse başarıyla kayıt oldunuz!");
                successAlert.setContentText(row.getCourseCode() + " - " + row.getCourseName() + 
                    "\n\nDers programınıza eklendi. Haftalık programınızdan görüntüleyebilirsiniz.");
                successAlert.showAndWait();
                loadCourses();
                break;
            case ADDED_TO_WAITLIST:
                showAlert(Alert.AlertType.INFORMATION, "Bekleme Listesi", 
                    "Kontenjan dolu. Bekleme listesine eklendiniz.");
                loadCourses();
                break;
            case PREREQUISITE_NOT_MET:
                showAlert(Alert.AlertType.WARNING, "Ön Koşul Eksik!", 
                    "Bu ders için ön koşul dersi (" + row.getPrerequisite() + ") tamamlanmamış!");
                break;
            case TIME_CONFLICT:
                showAlert(Alert.AlertType.WARNING, "Saat Çakışması!", 
                    "Bu ders saati mevcut programınızdaki başka bir dersle çakışıyor!");
                break;
            case ALREADY_ENROLLED:
                showAlert(Alert.AlertType.WARNING, "Zaten Kayıtlı", 
                    "Bu derse zaten kayıtlısınız!");
                break;
            default:
                showAlert(Alert.AlertType.ERROR, "Hata", result.getMessage());
        }
    }
    
    @FXML
    private void goToDashboard() {
        navigateTo("/fxml/student-dashboard.fxml");
    }
    
    @FXML
    private void goToSchedule() {
        navigateTo("/fxml/schedule.fxml");
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
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Row sınıfı
    public static class CourseRow {
        private int sectionId;
        private String courseCode, courseName, sectionNumber, instructor;
        private String day, time, room, capacity, credits, prerequisite, department;
        private boolean full, enrolled;
        
        public CourseRow(int sectionId, String courseCode, String courseName, String sectionNumber,
                         String instructor, String day, String time, String room, String capacity,
                         String credits, String prerequisite, boolean full, boolean enrolled, String department) {
            this.sectionId = sectionId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.sectionNumber = sectionNumber;
            this.instructor = instructor;
            this.day = day;
            this.time = time;
            this.room = room;
            this.capacity = capacity;
            this.credits = credits;
            this.prerequisite = prerequisite;
            this.full = full;
            this.enrolled = enrolled;
            this.department = department;
        }
        
        // Getters
        public int getSectionId() { return sectionId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getSectionNumber() { return sectionNumber; }
        public String getInstructor() { return instructor; }
        public String getDay() { return day; }
        public String getTime() { return time; }
        public String getRoom() { return room; }
        public String getCapacity() { return capacity; }
        public String getCredits() { return credits; }
        public String getPrerequisite() { return prerequisite; }
        public boolean isFull() { return full; }
        public boolean isEnrolled() { return enrolled; }
        public String getDepartment() { return department; }
    }
}
