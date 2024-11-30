package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.DaoModel;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize the DaoModel and check for user data (admin or regular user)
            DaoModel dao = new DaoModel();
            dao.checkAndInsertUser();        // Check and insert regular user if needed
            dao.checkAndInsertUserForAdmin(); // Check and insert admin user if needed

            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Movie Booking System");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}

