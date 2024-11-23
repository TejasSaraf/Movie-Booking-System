package controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class EmployeeHome {

	@FXML
	private Button logoutBtn, manageFilmsBtn;

	@FXML
	public void logoutButtonOnAction(ActionEvent event) {
		// Logic to navigate back to login page
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));

			// Get the current stage
			Stage stage = (Stage) logoutBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void manageFilmsButtonOnAction(ActionEvent event) {
		// Logic to navigate back to login page
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/FilmManagement.fxml"));

			// Get the current stage
			Stage stage = (Stage) manageFilmsBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
