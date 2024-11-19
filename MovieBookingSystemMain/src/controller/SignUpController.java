package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import model.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SignUpController {

	@FXML
	private Button signUpButton;

	@FXML
	private Button loginButton;

	@FXML
	private TextField txtFirstName;

	@FXML
	private TextField txtLastName;

	@FXML
	private TextField txtUserName;

	@FXML
	private TextField txtPassword;

	@FXML
	private Label lblStatus;

	private final DBConnect dbConnect = new DBConnect(); // Instance of DBConnect

	@FXML
	public void signUpButtonOnAction(ActionEvent event) {
		if (isInputValid()) {
			insertNewUser();
		} else {
			lblStatus.setText("Please fill in all fields.");
		}
	}

	private boolean isInputValid() {
		return !txtFirstName.getText().isBlank() && !txtLastName.getText().isBlank() && !txtUserName.getText().isBlank()
				&& !txtPassword.getText().isBlank();
	}

	private void insertNewUser() {
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		String userName = txtUserName.getText();
		String password = txtPassword.getText();

		String insertQuery = "INSERT INTO useraccounts (firstName, lastName, username, password) VALUES (?, ?, ?, ?)";

		try (Connection connectDB = dbConnect.connect();
				PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {

			// Set values dynamically
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, userName);
			preparedStatement.setString(4, password);

			// Execute update
			int rowsAffected = preparedStatement.executeUpdate();
			if (rowsAffected > 0) {
				lblStatus.setText("Sign-up successful! Please log in.");
			} else {
				lblStatus.setText("Sign-up failed. Please try again.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			lblStatus.setText("An error occurred during sign-up.");
		}
	}

	@FXML
	public void loginButtonOnAction(ActionEvent event) {
		// Logic to navigate back to login page
		try {
			javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
			javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
			stage.setScene(new javafx.scene.Scene(root));
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
			lblStatus.setText("Error navigating to login page.");
		}
	}
}
