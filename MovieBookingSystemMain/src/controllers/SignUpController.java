package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import models.DBConnect;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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

	@FXML
	private TextField txtEmailAddress;

	@FXML
	private DatePicker DateofBirth;

	@FXML
	private TextField txtAddress;

	@FXML
	private TextField txtPhoneNumber;

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
			javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
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
				&& validateUserName(txtUserName.getText()) && validatePassword(txtPassword.getText())
				&& validateEmail(txtEmailAddress.getText()) && validatePhoneNumber(txtPhoneNumber.getText())
				&& DateofBirth.getValue() != null && validateAddress(txtAddress.getText());
	}

	private void insertNewUser() {
		String firstName = txtFirstName.getText();
		String lastName = txtLastName.getText();
		String userName = txtUserName.getText();
		String plainPassword = txtPassword.getText();
		String emailAdderss = txtEmailAddress.getText();
		String address = txtAddress.getText();
		String phoneNo = txtPhoneNumber.getText();
		String dateOfBirth = DateofBirth.getValue() != null ? DateofBirth.getValue().toString() : "";

		// Hash the password
		String hashedPassword = hashPassword(plainPassword);
		if (hashedPassword == null) {
			lblStatus.setText("Error hashing the password.");
			return;
		}

		String insertQuery = "INSERT INTO useraccounts (firstName, lastName, username, password, emailaddress, dateofbirth, address, phoneNo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection connectDB = dbConnect.connect();
				PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery)) {

			// Set values dynamically
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, userName);
			preparedStatement.setString(4, hashedPassword);
			preparedStatement.setString(5, emailAdderss);
			preparedStatement.setString(6, dateOfBirth);
			preparedStatement.setString(7, address);
			preparedStatement.setString(8, phoneNo);

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
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
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

		txtEmailAddress.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateEmail(newValue)) {
				txtEmailAddress.setStyle("-fx-border-color: red;");
			} else {
				txtEmailAddress.setStyle(null);
			}
			updateSignUpButtonState();
		});

		txtPhoneNumber.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validatePhoneNumber(newValue)) {
				txtPhoneNumber.setStyle("-fx-border-color: red;");
			} else {
				txtPhoneNumber.setStyle(null);
			}
			updateSignUpButtonState();
		});

		DateofBirth.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				DateofBirth.setStyle("-fx-border-color: red;");
			} else {
				DateofBirth.setStyle(null);
			}
			updateSignUpButtonState();
		});

		txtAddress.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!validateAddress(newValue)) {
				txtAddress.setStyle("-fx-border-color: red;");
			} else {
				txtAddress.setStyle(null);
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

	private boolean validateEmail(String email) {
		if (email == null || email.isEmpty())
			return false;
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		return email.matches(emailRegex);
	}

	private boolean validatePhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.isEmpty())
			return false;
		String phoneRegex = "^(\\+\\d{1,2}\\s?)?\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$"; // Basic phone number
																							// validation
		return phoneNumber.matches(phoneRegex);
	}

	private boolean validateAddress(String address) {
		return address != null && !address.trim().isEmpty(); // Ensure address is not empty
	}
}
