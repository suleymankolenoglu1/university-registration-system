package com.university.controller;

import com.university.App;
import com.university.service.AuthService;
import com.university.service.LoginResult;
import com.university.model.Student;
import com.university.model.Instructor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Login ekranı controller sınıfı
 */
public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton instructorRadio;
    @FXML private ToggleGroup userTypeGroup;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    private AuthService authService;

    // Oturum açmış kullanıcıyı saklamak için static değişkenler
    private static Student currentStudent;
    private static Instructor currentInstructor;
    private static boolean isStudentLoggedIn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthService();
        
        // Enter tuşu ile giriş yapma
        passwordField.setOnAction(e -> handleLogin());
        emailField.setOnAction(e -> passwordField.requestFocus());
    }

    /**
     * Giriş yap butonuna tıklandığında
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Boş alan kontrolü
        if (email.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurun!");
            return;
        }

        // Giriş işlemi
        LoginResult result;
        if (studentRadio.isSelected()) {
            result = authService.loginStudent(email, password);
            if (result.isSuccess()) {
                currentStudent = (Student) result.getUser();
                currentInstructor = null;
                isStudentLoggedIn = true;
                navigateToStudentDashboard();
            }
        } else {
            result = authService.loginInstructor(email, password);
            if (result.isSuccess()) {
                currentInstructor = (Instructor) result.getUser();
                currentStudent = null;
                isStudentLoggedIn = false;
                navigateToInstructorDashboard();
            }
        }

        if (!result.isSuccess()) {
            showError(result.getMessage());
        }
    }

    /**
     * Kayıt ol butonuna tıklandığında
     */
    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(root, 900, 650);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            showError("Kayıt sayfası yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

    /**
     * Öğrenci paneline yönlendir
     */
    private void navigateToStudentDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/student-dashboard.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = App.getPrimaryStage();
            stage.setTitle("🎓 Öğrenci Paneli - " + currentStudent.getFullName());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Dashboard yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

    /**
     * Öğretim elemanı paneline yönlendir
     */
    private void navigateToInstructorDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/instructor-dashboard.fxml"));
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = App.getPrimaryStage();
            stage.setTitle("👨‍🏫 Öğretim Elemanı Paneli - " + currentInstructor.getFullName());
            stage.setScene(scene);
        } catch (Exception e) {
            showError("Dashboard yüklenirken hata oluştu!");
            e.printStackTrace();
        }
    }

    /**
     * Hata mesajı göster
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    // Static getter metodları - diğer controller'ların kullanımı için
    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static Instructor getCurrentInstructor() {
        return currentInstructor;
    }

    public static boolean isStudentSession() {
        return isStudentLoggedIn;
    }

    /**
     * Oturumu kapat
     */
    public static void logout() {
        currentStudent = null;
        currentInstructor = null;
        isStudentLoggedIn = false;
    }
}
