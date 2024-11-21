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
			DaoModel dao = new DaoModel();
			dao.checkAndInsertUser();
			dao.checkAndInsertUserForAdmin();
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
		launch(args);
	}
}
