package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import model.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;

public class LoginController {

	@FXML
	private Label lblStatus;

	@FXML
	private TextField txtUserName;

	@FXML
	private TextField txtPassword;

	@FXML
	private Button loginButton;

	@FXML
	private Button signUpButton;

	private final DBConnect dbConnect = new DBConnect(); // Instance of DBConnect

	@FXML
	public void initialize() {
		// Add validation listeners
		addValidationListeners();
		updateLoginButtonState();
	}

	@FXML
	public void loginButtonOnAction(ActionEvent e) {
		if (validateLoginFields()) {
			validateLogin();
		} else {
			lblStatus.setText("Please correct the errors and try again.");
		}
	}

	private boolean validateLoginFields() {
		return validateUserName(txtUserName.getText()) && validatePassword(txtPassword.getText());
	}

	private boolean validateUserName(String userName) {
		if (userName == null || userName.isEmpty()) {
			lblStatus.setText("Username cannot be empty.");
			return false;
		}
		if (!userName.matches("\\w{4,}")) { // At least 4 characters, alphanumeric only
			lblStatus.setText("Username must be at least 4 characters and alphanumeric.");
			return false;
		}
		return true;
	}

	private boolean validatePassword(String password) {
		if (password == null || password.isEmpty()) {
			lblStatus.setText("Password cannot be empty.");
			return false;
		}
		String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$";
		if (!password.matches(passwordRegex)) {
			lblStatus.setText(
					"Password must be 8-16 characters, include uppercase, lowercase, a digit, and a special character.");
			return false;
		}
		return true;
	}

	private void validateLogin() {
		try {
			// Get the database connection
			Connection connectDB = dbConnect.connect();

			// SQL query to verify user credentials
			String verifyLogin = "SELECT COUNT(1) FROM UserAccounts WHERE username = ? AND password = ?";
			try (java.sql.PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin)) {
				preparedStatement.setString(1, txtUserName.getText());
				preparedStatement.setString(2, txtPassword.getText());

				ResultSet queryResult = preparedStatement.executeQuery();
				if (queryResult.next() && queryResult.getInt(1) == 1) {
					javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
					javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 400);
					javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
					stage.setScene(scene);
					stage.show();
				} else {
					lblStatus.setText("Invalid Username or Password!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			lblStatus.setText("An error occurred during login.");
		}
	}

	@FXML
	public void signUpButtonOnAction(ActionEvent e) {
		try {
			javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/SignUp.fxml"));
			javafx.scene.Scene scene = new javafx.scene.Scene(root);
			javafx.stage.Stage stage = (javafx.stage.Stage) signUpButton.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
			lblStatus.setText("Error navigating to sign-up page.");
		}
	}

	private void addValidationListeners() {
		txtUserName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateUserName(newValue)) {
				txtUserName.setStyle("-fx-border-color: red;");
			} else {
				txtUserName.setStyle(null);
			}
			updateLoginButtonState();
		});

		txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validatePassword(newValue)) {
				txtPassword.setStyle("-fx-border-color: red;");
			} else {
				txtPassword.setStyle(null);
			}
			updateLoginButtonState();
		});
	}

	private void updateLoginButtonState() {
		loginButton.setDisable(!validateLoginFields());
	}
}
