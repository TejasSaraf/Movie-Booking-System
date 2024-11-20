package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import models.DBConnect;

import java.security.MessageDigest;
import java.util.Base64;
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

			// SQL query to get the hashed password for the entered username
			String getPasswordQuery = "SELECT password FROM UserAccounts WHERE username = ?";
			try (java.sql.PreparedStatement preparedStatement = connectDB.prepareStatement(getPasswordQuery)) {
				preparedStatement.setString(1, txtUserName.getText());

				ResultSet queryResult = preparedStatement.executeQuery();

				if (queryResult.next()) {
					String storedHashedPassword = queryResult.getString("password");
					String enteredHashedPassword = hashPassword(txtPassword.getText());

					if (storedHashedPassword.equals(enteredHashedPassword)) {
						// Password matches, proceed to main page
						javafx.scene.Parent root = javafx.fxml.FXMLLoader
								.load(getClass().getResource("/views/Main.fxml"));
						javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 400);
						javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
						stage.setScene(scene);
						stage.show();
					} else {
						lblStatus.setText("Invalid Username or Password!");
					}
				} else {
					lblStatus.setText("Invalid Username or Password!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			lblStatus.setText("An error occurred during login.");
		}
	}

	/**
	 * Hashes a password using SHA-256 and encodes it in Base64.
	 *
	 * @param password the plain text password to hash.
	 * @return the hashed password as a Base64 encoded string, or null if an error
	 *         occurs.
	 */
	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());
			return Base64.getEncoder().encodeToString(hashedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
