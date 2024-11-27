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
	
	@FXML
	private Button logoutBtn;
	
	@FXML
	private Button manageFilmsBtn;
	
	@FXML
	private Button exportBookingsBtn;

	private final DBConnect dbConnect = new DBConnect(); // Instance of DBConnect
	public static boolean checkAdmin = false;

	@FXML
	public void initialize() {
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

			// Check login details for both users and admins
			if (checkCredentials(connectDB, "useraccounts")) {
				lblStatus.setText("User login successful!");
				navigateToMainPage("/views/ViewFilms.fxml");
			} else if (checkCredentials(connectDB, "adminaccounts")) {
				lblStatus.setText("Admin login successful!");
				navigateToMainPage("/views/EmployeeHome.fxml");
				checkAdmin = true;
			} else {
				lblStatus.setText("Invalid Username or Password!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			lblStatus.setText("An error occurred during login.");
		}
	}

	private boolean checkCredentials(Connection connectDB, String tableName) throws Exception {
		String getPasswordQuery = "SELECT password FROM " + tableName + " WHERE username = ?";
		try (java.sql.PreparedStatement preparedStatement = connectDB.prepareStatement(getPasswordQuery)) {
			preparedStatement.setString(1, txtUserName.getText());
			ResultSet queryResult = preparedStatement.executeQuery();

			if (queryResult.next()) {
				String storedHashedPassword = queryResult.getString("password");
				String enteredHashedPassword = hashPassword(txtPassword.getText());
				return storedHashedPassword.equals(enteredHashedPassword);
			}
		}
		return false;
	}

	private void navigateToMainPage(String fxmlPath) {
		try {
			javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(fxmlPath));
			javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 400);
			javafx.stage.Stage stage = (javafx.stage.Stage) loginButton.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (Exception ex) {
			ex.printStackTrace();
			lblStatus.setText("Error navigating to main page.");
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
			javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/views/SignUp.fxml"));
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
