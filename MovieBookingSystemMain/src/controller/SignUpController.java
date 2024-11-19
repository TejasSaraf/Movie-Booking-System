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
	public void initialize() {
		// Add validation listeners
		addValidationListeners();
		updateSignUpButtonState();
	}

	@FXML
	public void signUpButtonOnAction(ActionEvent event) {
		if (isInputValid()) {
			insertNewUser();
		} else {
			lblStatus.setText("Please fill in all fields correctly.");
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

	private boolean isInputValid() {
		return validateName(txtFirstName.getText()) && validateName(txtLastName.getText())
				&& validateUserName(txtUserName.getText()) && validatePassword(txtPassword.getText());
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

	private void addValidationListeners() {
		txtFirstName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateName(newValue)) {
				txtFirstName.setStyle("-fx-border-color: red;");
			} else {
				txtFirstName.setStyle(null);
			}
			updateSignUpButtonState();
		});

		txtLastName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateName(newValue)) {
				txtLastName.setStyle("-fx-border-color: red;");
			} else {
				txtLastName.setStyle(null);
			}
			updateSignUpButtonState();
		});

		txtUserName.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateUserName(newValue)) {
				txtUserName.setStyle("-fx-border-color: red;");
			} else {
				txtUserName.setStyle(null);
			}
			updateSignUpButtonState();
		});

		txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validatePassword(newValue)) {
				txtPassword.setStyle("-fx-border-color: red;");
			} else {
				txtPassword.setStyle(null);
			}
			updateSignUpButtonState();
		});
	}

	private void updateSignUpButtonState() {
		signUpButton.setDisable(!isInputValid());
	}

	private boolean validateName(String name) {
		return name != null && name.matches("[A-Za-z]{2,}"); // Only alphabets, at least 2 characters
	}

	private boolean validateUserName(String userName) {
		return userName != null && userName.matches("\\w{4,}"); // At least 4 characters, no special symbols
	}

	private boolean validatePassword(String password) {
		if (password == null)
			return false;
		String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}$";
		return password.matches(passwordRegex);
	}

}
