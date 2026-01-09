package com.university.controller;

import com.university.App;
import com.university.model.*;
import com.university.dao.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;

/**
 * Öğretim Elemanı Dashboard Controller
 * Her menü butonu için farklı içerik gösterir
 */
public class InstructorDashboardController implements Initializable {

    @FXML private Label instructorNameLabel;
    @FXML private Label instructorDeptLabel;
    @FXML private Label pageTitle;
    @FXML private VBox contentArea;
    @FXML private HBox statsBox;
    
    // Dinamik içerik elemanları
    @FXML private Label sectionTitle;
    @FXML private Label tableTitle;
    @FXML private HBox statsCards;
    
    @FXML private Label courseCountLabel;
    @FXML private Label sectionCountLabel;
    @FXML private Label studentCountLabel;
    
    // Dersler tablosu
    @FXML private TableView<InstructorCourseRow> coursesTable;
    @FXML private TableColumn<InstructorCourseRow, String> colCourseCode;
    @FXML private TableColumn<InstructorCourseRow, String> colCourseName;
    @FXML private TableColumn<InstructorCourseRow, String> colSection;
    @FXML private TableColumn<InstructorCourseRow, String> colDay;
    @FXML private TableColumn<InstructorCourseRow, String> colTime;
    @FXML private TableColumn<InstructorCourseRow, String> colRoom;
    @FXML private TableColumn<InstructorCourseRow, String> colEnrolled;
    @FXML private TableColumn<InstructorCourseRow, String> colCapacity;
    @FXML private TableColumn<InstructorCourseRow, Void> colAction;
    
    // Öğrenci tablosu
    @FXML private TableView<StudentRow> studentsTable;
    @FXML private TableColumn<StudentRow, String> colStudentNo;
    @FXML private TableColumn<StudentRow, String> colStudentName;
    @FXML private TableColumn<StudentRow, String> colStudentDept;
    @FXML private TableColumn<StudentRow, String> colStudentEmail;
    
    // Program grid
    @FXML private GridPane scheduleGrid;
    
    @FXML private Button menuDashboard;
    @FXML private Button menuMyCourses;
    @FXML private Button menuStudents;
    @FXML private Button menuGrades;
    @FXML private Button menuSchedule;
    
    private SectionDAO sectionDAO;
    private CourseDAO courseDAO;
    private RoomDAO roomDAO;
    private EnrollmentDAO enrollmentDAO;
    private StudentDAO studentDAO;
    
    private Instructor currentInstructor;
    private List<Section> instructorSections = new ArrayList<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sectionDAO = new SectionDAO();
        courseDAO = new CourseDAO();
        roomDAO = new RoomDAO();
        enrollmentDAO = new EnrollmentDAO();
        studentDAO = new StudentDAO();
        
        currentInstructor = LoginController.getCurrentInstructor();
        
        if (currentInstructor != null) {
            instructorNameLabel.setText(currentInstructor.getFullName());
            instructorDeptLabel.setText(currentInstructor.getDepartment());
        }
        
        setupTableColumns();
        setupStudentTableColumns();
        showDashboard(); // Varsayılan olarak ana sayfa göster
    }
    
    private void setupTableColumns() {
        colCourseCode.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        colCourseName.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        colSection.setCellValueFactory(new PropertyValueFactory<>("sectionNumber"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("day"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colEnrolled.setCellValueFactory(new PropertyValueFactory<>("enrolled"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        // İşlem sütunu - Silme butonu
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("🗑️ Sil");
            {
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 5;");
                deleteBtn.setOnAction(event -> {
                    InstructorCourseRow row = getTableView().getItems().get(getIndex());
                    handleDeleteSection(row);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteBtn);
            }
        });
    }
    
    private void setupStudentTableColumns() {
        colStudentNo.setCellValueFactory(new PropertyValueFactory<>("studentNo"));
        colStudentName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colStudentDept.setCellValueFactory(new PropertyValueFactory<>("department"));
        colStudentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
    
    private void loadInstructorSections() {
        if (currentInstructor == null) return;
        try {
            instructorSections = sectionDAO.findByInstructor(currentInstructor.getInstructorId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ===== ANA SAYFA =====
    @FXML
    private void showDashboard() {
        setActiveMenu(menuDashboard);
        loadInstructorSections();
        
        // Elemanları göster/gizle
        sectionTitle.setText("📊 Eğitim İstatistikleri");
        tableTitle.setText("Verdiğim Dersler");
        tableTitle.setVisible(true);
        statsCards.setVisible(true);
        statsCards.setManaged(true);
        coursesTable.setVisible(true);
        coursesTable.setManaged(true);
        studentsTable.setVisible(false);
        studentsTable.setManaged(false);
        scheduleGrid.setVisible(false);
        scheduleGrid.setManaged(false);
        colAction.setVisible(false); // Ana sayfada silme butonu gizli
        
        // Ders seçim combosu varsa kaldır
        removeSectionSelector();
        
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        if (currentInstructor == null) return;
        
        try {
            Set<Integer> uniqueCourses = new HashSet<>();
            int totalStudents = 0;
            
            ObservableList<InstructorCourseRow> rows = FXCollections.observableArrayList();
            
            for (Section section : instructorSections) {
                Course course = courseDAO.findById(section.getCourseId());
                Room room = section.getRoomId() != null ? roomDAO.findById(section.getRoomId()) : null;
                
                if (course != null) {
                    uniqueCourses.add(course.getCourseId());
                    totalStudents += section.getEnrolledCount();
                    
                    InstructorCourseRow row = new InstructorCourseRow(
                        section.getSectionId(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        String.valueOf(section.getSectionNumber()),
                        section.getDayOfWeek(),
                        section.getStartTime() + " - " + section.getEndTime(),
                        room != null ? room.getRoomCode() : "TBA",
                        String.valueOf(section.getEnrolledCount()),
                        String.valueOf(section.getCapacity())
                    );
                    rows.add(row);
                }
            }
            
            courseCountLabel.setText(String.valueOf(uniqueCourses.size()));
            sectionCountLabel.setText(String.valueOf(instructorSections.size()));
            studentCountLabel.setText(String.valueOf(totalStudents));
            
            coursesTable.setItems(rows);
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Veriler yüklenirken hata: " + e.getMessage());
        }
    }
    
    // ===== DERSLERİM =====
    @FXML
    private void showMyCourses() {
        setActiveMenu(menuMyCourses);
        loadInstructorSections();
        
        sectionTitle.setText("📚 Verdiğim Dersler");
        tableTitle.setText("Ders Şubeleri (Silmek için İşlem sütununu kullanın)");
        tableTitle.setVisible(true);
        statsCards.setVisible(false);
        statsCards.setManaged(false);
        coursesTable.setVisible(true);
        coursesTable.setManaged(true);
        studentsTable.setVisible(false);
        studentsTable.setManaged(false);
        scheduleGrid.setVisible(false);
        scheduleGrid.setManaged(false);
        colAction.setVisible(true); // Derslerimde silme butonu görünür
        
        removeSectionSelector();
        loadDashboardData();
    }
    
    private void handleDeleteSection(InstructorCourseRow row) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Ders Silme Onayı");
        confirm.setHeaderText(row.getCourseCode() + " - " + row.getCourseName());
        confirm.setContentText("Bu ders şubesini silmek istediğinize emin misiniz?\nKayıtlı öğrenciler: " + row.getEnrolled());
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sectionDAO.delete(row.getSectionId());
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Ders şubesi silindi!");
                showMyCourses();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Silinirken hata: " + e.getMessage());
            }
        }
    }
    
    // ===== ÖĞRENCİLER =====
    @FXML
    private void showStudents() {
        setActiveMenu(menuStudents);
        loadInstructorSections();
        
        sectionTitle.setText("👥 Kayıtlı Öğrenciler");
        tableTitle.setText("Ders seçerek öğrenci listesini görüntüleyin");
        tableTitle.setVisible(true);
        statsCards.setVisible(false);
        statsCards.setManaged(false);
        coursesTable.setVisible(false);
        coursesTable.setManaged(false);
        studentsTable.setVisible(true);
        studentsTable.setManaged(true);
        scheduleGrid.setVisible(false);
        scheduleGrid.setManaged(false);
        
        loadStudentView();
    }
    
    private void removeSectionSelector() {
        contentArea.getChildren().removeIf(node -> "sectionSelector".equals(node.getId()));
    }
    
    private void loadStudentView() {
        // Önceki combobox varsa kaldır
        removeSectionSelector();
        
        HBox selectorBox = new HBox(15);
        selectorBox.setId("sectionSelector");
        selectorBox.setAlignment(Pos.CENTER_LEFT);
        selectorBox.setPadding(new Insets(0, 0, 10, 0));
        
        Label selectLabel = new Label("Ders Seçin:");
        selectLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        ComboBox<String> sectionCombo = new ComboBox<>();
        sectionCombo.setPrefWidth(400);
        sectionCombo.setPromptText("Öğrenci listesini görmek için ders seçin");
        
        Map<String, Integer> sectionMap = new HashMap<>();
        
        try {
            for (Section section : instructorSections) {
                Course course = courseDAO.findById(section.getCourseId());
                if (course != null) {
                    String display = course.getCourseCode() + " - " + course.getCourseName() + 
                                   " (Şube " + section.getSectionNumber() + ")";
                    sectionCombo.getItems().add(display);
                    sectionMap.put(display, section.getSectionId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        sectionCombo.setOnAction(e -> {
            String selected = sectionCombo.getValue();
            if (selected != null && sectionMap.containsKey(selected)) {
                loadStudentsForSection(sectionMap.get(selected));
            }
        });
        
        selectorBox.getChildren().addAll(selectLabel, sectionCombo);
        
        // studentsTable'dan önce ekle
        int index = contentArea.getChildren().indexOf(studentsTable);
        if (index >= 0) {
            contentArea.getChildren().add(index, selectorBox);
        }
        
        studentsTable.getItems().clear();
    }
    
    private void loadStudentsForSection(int sectionId) {
        ObservableList<StudentRow> rows = FXCollections.observableArrayList();
        
        try {
            List<Enrollment> enrollments = enrollmentDAO.findBySection(sectionId);
            
            for (Enrollment enrollment : enrollments) {
                Student student = studentDAO.findById(enrollment.getStudentId());
                if (student != null) {
                    rows.add(new StudentRow(
                        student.getStudentNumber(),
                        student.getFullName(),
                        student.getDepartment(),
                        student.getEmail()
                    ));
                }
            }
            
            studentsTable.setItems(rows);
            tableTitle.setText("Toplam " + rows.size() + " öğrenci kayıtlı");
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Hata", "Öğrenciler yüklenirken hata: " + e.getMessage());
        }
    }
    
    // ===== PROGRAMIM =====
    @FXML
    private void showSchedule() {
        setActiveMenu(menuSchedule);
        loadInstructorSections();
        
        sectionTitle.setText("📅 Haftalık Ders Programım");
        tableTitle.setVisible(false);
        statsCards.setVisible(false);
        statsCards.setManaged(false);
        coursesTable.setVisible(false);
        coursesTable.setManaged(false);
        studentsTable.setVisible(false);
        studentsTable.setManaged(false);
        scheduleGrid.setVisible(true);
        scheduleGrid.setManaged(true);
        
        removeSectionSelector();
        buildScheduleGrid();
    }
    
    private void buildScheduleGrid() {
        scheduleGrid.getChildren().clear();
        scheduleGrid.getColumnConstraints().clear();
        scheduleGrid.getRowConstraints().clear();
        
        String[] days = {"", "PAZARTESİ", "SALI", "ÇARŞAMBA", "PERŞEMBE", "CUMA"};
        String[] hours = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
        
        // Sütun constraints
        for (int i = 0; i < 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(i == 0 ? 10 : 18);
            scheduleGrid.getColumnConstraints().add(col);
        }
        
        // Satır constraints
        for (int i = 0; i < hours.length + 1; i++) {
            RowConstraints row = new RowConstraints();
            row.setMinHeight(60);
            scheduleGrid.getRowConstraints().add(row);
        }
        
        // Başlık satırı
        for (int col = 0; col < days.length; col++) {
            Label label = new Label(days[col]);
            label.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-alignment: center;");
            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            scheduleGrid.add(label, col, 0);
        }
        
        // Saat satırları
        for (int row = 0; row < hours.length; row++) {
            Label hourLabel = new Label(hours[row]);
            hourLabel.setStyle("-fx-background-color: #E8F5E9; -fx-font-weight: bold; -fx-padding: 5; -fx-alignment: center;");
            hourLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            hourLabel.setAlignment(Pos.CENTER);
            scheduleGrid.add(hourLabel, 0, row + 1);
            
            // Boş hücreler
            for (int col = 1; col < 6; col++) {
                Label emptyCell = new Label("");
                emptyCell.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 0.5;");
                emptyCell.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                scheduleGrid.add(emptyCell, col, row + 1);
            }
        }
        
        // Dersleri yerleştir
        try {
            for (Section section : instructorSections) {
                Course course = courseDAO.findById(section.getCourseId());
                if (course == null || section.getDayOfWeek() == null) continue;
                
                int col = getDayColumn(section.getDayOfWeek());
                int startRow = getHourRow(section.getStartTime().toString());
                int endRow = getHourRow(section.getEndTime().toString());
                
                if (col > 0 && startRow > 0 && endRow > startRow) {
                    VBox courseCard = new VBox(3);
                    courseCard.setAlignment(Pos.CENTER);
                    courseCard.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5; -fx-padding: 5;");
                    
                    Label codeLabel = new Label(course.getCourseCode());
                    codeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
                    
                    Label nameLabel = new Label(course.getCourseName());
                    nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 9px;");
                    nameLabel.setWrapText(true);
                    
                    courseCard.getChildren().addAll(codeLabel, nameLabel);
                    courseCard.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                    
                    scheduleGrid.add(courseCard, col, startRow, 1, endRow - startRow);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int getDayColumn(String day) {
        return switch (day.toUpperCase()) {
            case "PAZARTESI" -> 1;
            case "SALI" -> 2;
            case "CARSAMBA", "ÇARŞAMBA" -> 3;
            case "PERSEMBE", "PERŞEMBE" -> 4;
            case "CUMA" -> 5;
            default -> 0;
        };
    }
    
    private int getHourRow(String time) {
        String hour = time.substring(0, 2);
        return switch (hour) {
            case "09" -> 1;
            case "10" -> 2;
            case "11" -> 3;
            case "12" -> 4;
            case "13" -> 5;
            case "14" -> 6;
            case "15" -> 7;
            case "16" -> 8;
            case "17" -> 9;
            default -> 0;
        };
    }
    
    @FXML
    private void showGrades() {
        setActiveMenu(menuGrades);
        pageTitle.setText("📝 Not Girişi");
        // TODO: Not girişi görünümü - ileride eklenebilir
    }
    
    @FXML
    private void showCreateSection() {
        // Ders oluşturma dialogu
        Dialog<Section> dialog = new Dialog<>();
        dialog.setTitle("Yeni Ders Şubesi Oluştur");
        dialog.setHeaderText("Vermek istediğiniz dersi seçin ve detayları girin");
        
        // Butonlar
        ButtonType createButtonType = new ButtonType("Oluştur", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 150, 10, 20));
        
        // Ders seçimi
        ComboBox<CourseItem> courseCombo = new ComboBox<>();
        try {
            List<Course> courses = courseDAO.findByDepartment(currentInstructor.getDepartment());
            for (Course c : courses) {
                courseCombo.getItems().add(new CourseItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        courseCombo.setPromptText("Ders seçin");
        courseCombo.setPrefWidth(300);
        
        // Gün seçimi
        ComboBox<String> dayCombo = new ComboBox<>();
        dayCombo.getItems().addAll("PAZARTESI", "SALI", "CARSAMBA", "PERSEMBE", "CUMA");
        dayCombo.setPromptText("Gün seçin");
        
        // Saat seçimi
        ComboBox<String> startTimeCombo = new ComboBox<>();
        startTimeCombo.getItems().addAll("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00");
        startTimeCombo.setPromptText("Başlangıç");
        
        ComboBox<String> endTimeCombo = new ComboBox<>();
        endTimeCombo.getItems().addAll("10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00");
        endTimeCombo.setPromptText("Bitiş");
        
        // Derslik seçimi
        ComboBox<RoomItem> roomCombo = new ComboBox<>();
        try {
            List<Room> rooms = roomDAO.findAll();
            for (Room r : rooms) {
                roomCombo.getItems().add(new RoomItem(r.getRoomId(), r.getRoomCode() + " (" + r.getCapacity() + " kişilik)"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        roomCombo.setPromptText("Derslik seçin");
        
        // Kontenjan
        Spinner<Integer> capacitySpinner = new Spinner<>(10, 200, 30, 5);
        capacitySpinner.setEditable(true);
        
        // Grid'e ekle
        grid.add(new Label("Ders:"), 0, 0);
        grid.add(courseCombo, 1, 0);
        grid.add(new Label("Gün:"), 0, 1);
        grid.add(dayCombo, 1, 1);
        grid.add(new Label("Başlangıç Saati:"), 0, 2);
        grid.add(startTimeCombo, 1, 2);
        grid.add(new Label("Bitiş Saati:"), 0, 3);
        grid.add(endTimeCombo, 1, 3);
        grid.add(new Label("Derslik:"), 0, 4);
        grid.add(roomCombo, 1, 4);
        grid.add(new Label("Kontenjan:"), 0, 5);
        grid.add(capacitySpinner, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        // Sonuç işleme
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (courseCombo.getValue() == null || dayCombo.getValue() == null || 
                    startTimeCombo.getValue() == null || endTimeCombo.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Eksik Bilgi", "Lütfen tüm alanları doldurun!");
                    return null;
                }
                
                Section section = new Section();
                section.setCourseId(courseCombo.getValue().getId());
                section.setInstructorId(currentInstructor.getInstructorId());
                section.setRoomId(roomCombo.getValue() != null ? roomCombo.getValue().getId() : null);
                section.setSemester("2025-BAHAR");
                section.setSectionNumber(getNextSectionNumber(courseCombo.getValue().getId()));
                section.setDayOfWeek(dayCombo.getValue());
                section.setStartTime(LocalTime.parse(startTimeCombo.getValue()));
                section.setEndTime(LocalTime.parse(endTimeCombo.getValue()));
                section.setCapacity(capacitySpinner.getValue());
                
                return section;
            }
            return null;
        });
        
        Optional<Section> result = dialog.showAndWait();
        
        result.ifPresent(section -> {
            try {
                int sectionId = sectionDAO.create(section);
                if (sectionId > 0) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Başarılı! ✅");
                    successAlert.setHeaderText("Ders şubesi oluşturuldu!");
                    successAlert.setContentText("Ders başarıyla programa eklendi.\nÖğrenciler artık bu derse kayıt olabilir.");
                    successAlert.showAndWait();
                    showDashboard();
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Hata", "Ders oluşturulurken hata: " + e.getMessage());
            }
        });
    }
    
    private int getNextSectionNumber(int courseId) {
        try {
            List<Section> existing = sectionDAO.findByCourse(courseId);
            return existing.size() + 1;
        } catch (SQLException e) {
            return 1;
        }
    }
    
    // Yardımcı sınıflar
    private static class CourseItem {
        private int id;
        private String display;
        public CourseItem(int id, String display) { this.id = id; this.display = display; }
        public int getId() { return id; }
        @Override public String toString() { return display; }
    }
    
    private static class RoomItem {
        private int id;
        private String display;
        public RoomItem(int id, String display) { this.id = id; this.display = display; }
        public int getId() { return id; }
        @Override public String toString() { return display; }
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
        menuMyCourses.getStyleClass().remove("menu-button-active");
        menuStudents.getStyleClass().remove("menu-button-active");
        if (menuGrades != null) menuGrades.getStyleClass().remove("menu-button-active");
        menuSchedule.getStyleClass().remove("menu-button-active");
        activeButton.getStyleClass().add("menu-button-active");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Row sınıfları
    public static class InstructorCourseRow {
        private int sectionId;
        private String courseCode, courseName, sectionNumber, day, time, room, enrolled, capacity;
        
        public InstructorCourseRow(int sectionId, String courseCode, String courseName, 
                                    String sectionNumber, String day, String time, 
                                    String room, String enrolled, String capacity) {
            this.sectionId = sectionId;
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.sectionNumber = sectionNumber;
            this.day = day;
            this.time = time;
            this.room = room;
            this.enrolled = enrolled;
            this.capacity = capacity;
        }
        
        public int getSectionId() { return sectionId; }
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getSectionNumber() { return sectionNumber; }
        public String getDay() { return day; }
        public String getTime() { return time; }
        public String getRoom() { return room; }
        public String getEnrolled() { return enrolled; }
        public String getCapacity() { return capacity; }
    }
    
    public static class StudentRow {
        private String studentNo, fullName, department, email;
        
        public StudentRow(String studentNo, String fullName, String department, String email) {
            this.studentNo = studentNo;
            this.fullName = fullName;
            this.department = department;
            this.email = email;
        }
        
        public String getStudentNo() { return studentNo; }
        public String getFullName() { return fullName; }
        public String getDepartment() { return department; }
        public String getEmail() { return email; }
    }
}
