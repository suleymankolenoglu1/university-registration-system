package com.university;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.university.config.DatabaseConnection;

/**
 * Akıllı Üniversite Ders Kayıt ve Programı Sistemi
 * Ana JavaFX Uygulama Sınıfı
 */
public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Login ekranını yükle
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("🎓 Üniversite Ders Kayıt Sistemi");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    /**
     * Sahne değiştirme metodu
     */
    public static void changeScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource("/fxml/" + fxmlFile));
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(App.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ana pencereyi döndür
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        // Uygulama kapanırken veritabanı bağlantısını kapat
        DatabaseConnection.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
