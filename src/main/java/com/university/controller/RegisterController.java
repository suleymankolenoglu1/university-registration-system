package com.university.controller;

import com.university.App;
import com.university.service.AuthService;
import com.university.service.LoginResult;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Kayıt Ekranı Controller
 */
public class RegisterController implements Initializable {

    @FXML private RadioButton studentRadio;
    @FXML private RadioButton instructorRadio;
    @FXML private ToggleGroup userTypeGroup;
    
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField numberField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> departmentCombo;
    
    @FXML private HBox semesterBox;
    @FXML private ComboBox<Integer> semesterCombo;
    
    @FXML private HBox titleBox;
    @FXML private ComboBox<String> titleCombo;
    
    @FXML private Button uploadButton;
    @FXML private Label fileNameLabel;
    @FXML private Label errorLabel;
    
    private AuthService authService;
    private File selectedFile;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        authService = new AuthService();
        
        // Bölümler - Yazılım Mühendisliği en başta
        departmentCombo.setItems(FXCollections.observableArrayList(
            "Yazılım Mühendisliği",
            "Bilgisayar Mühendisliği",
            "Elektrik Elektronik Mühendisliği",
            "Makine Mühendisliği",
            "İnşaat Mühendisliği",
            "Matematik",
            "Fizik",
            "Kimya"
        ));
        departmentCombo.setValue("Yazılım Mühendisliği");
        
        // Sınıflar (1-4)
        semesterCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        
        // Ünvanlar
        titleCombo.setItems(FXCollections.observableArrayList(
            "Prof. Dr.",
            "Doç. Dr.",
            "Dr. Öğr. Üyesi",
            "Öğr. Gör.",
            "Arş. Gör."
        ));
        
        // Kullanıcı tipi değiştiğinde form alanlarını güncelle
        userTypeGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == studentRadio) {
                semesterBox.setVisible(true);
                semesterBox.setManaged(true);
                titleBox.setVisible(false);
                titleBox.setManaged(false);
            } else {
                semesterBox.setVisible(false);
                semesterBox.setManaged(false);
                titleBox.setVisible(true);
                titleBox.setManaged(true);
            }
        });
    }
    
    @FXML
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Öğrenci/Personel Belgesi Seçin");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Dosyaları", "*.pdf")
        );
        
        File file = fileChooser.showOpenDialog(App.getPrimaryStage());
        
        if (file != null) {
            selectedFile = file;
            fileNameLabel.setText("✅ " + file.getName());
            fileNameLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 12px; -fx-font-weight: bold;");
            uploadButton.setText("📄 Dosya Seçildi");
            uploadButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        }
    }
    
    @FXML
    private void handleRegister() {
        // Validasyonlar
        if (firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            showError("Ad ve soyad boş olamaz!");
            return;
        }
        
        if (numberField.getText().trim().isEmpty()) {
            showError("Numara boş olamaz!");
            return;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showError("E-posta boş olamaz!");
            return;
        }
        
        if (passwordField.getText().length() < 6) {
            showError("Şifre en az 6 karakter olmalı!");
            return;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Şifreler eşleşmiyor!");
            return;
        }
        
        if (departmentCombo.getValue() == null) {
            showError("Bölüm seçiniz!");
            return;
        }
        
        // PDF kontrolü
        if (selectedFile == null) {
            showError("Lütfen öğrenci/personel belgenizi PDF olarak yükleyin!");
            return;
        }
        
        LoginResult result;
        
        if (studentRadio.isSelected()) {
            if (semesterCombo.getValue() == null) {
                showError("Sınıf seçiniz!");
                return;
            }
            
            // Sınıftan dönem hesapla (1.sınıf=1, 2.sınıf=3, 3.sınıf=5, 4.sınıf=7 - Güz dönemi)
            int sinif = semesterCombo.getValue();
            int donem = (sinif * 2) - 1; // Güz dönemi olarak kaydet
            
            result = authService.registerStudent(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                numberField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                departmentCombo.getValue(),
                donem
            );
        } else {
            if (titleCombo.getValue() == null) {
                showError("Ünvan seçiniz!");
                return;
            }
            
            result = authService.registerInstructor(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                numberField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText(),
                titleCombo.getValue(),
                departmentCombo.getValue()
            );
        }
        
        if (result.isSuccess()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Kayıt Başarılı!");
            alert.setHeaderText("Hoş geldiniz! 🎉");
            alert.setContentText("Hesabınız başarıyla oluşturuldu.\nŞimdi giriş yapabilirsiniz.");
            alert.showAndWait();
            goToLogin();
        } else {
            showError(result.getMessage());
        }
    }
    
    @FXML
    private void goToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            App.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }
}
