package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.DBConnect;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.Node;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateFilm implements Initializable {

	@FXML
	private Button addFilmButton;
	@FXML
	private TextField trailerField;
	@FXML
	private TextField titleField;
	@FXML
	private TextArea descriptionArea;
	@FXML
	private DatePicker fromDate;
	@FXML
	private DatePicker toDate;
	@FXML
	private ComboBox<String> timeChoice1;
	@FXML
	private ComboBox<String> timeChoice2;
	@FXML
	private ComboBox<String> timeChoice3;
	@FXML
	private ComboBox<String> ageRatingChoice;
	@FXML
	private TextField imdbRatingField;
	@FXML
	private Button uploadImageButton;
	@FXML
	private ImageView uploadedFilmPoster;
	@FXML
	private Button homeBtn;

	@FXML
	private Label txtTitle;

	@FXML
	private Label lblFrom;
	@FXML
	private Label lblTo;
	@FXML
	private Label lblTime1;
	@FXML
	private Label lblTime2;
	@FXML
	private Label lblTime3;
	@FXML
	private Label lblAgeRating;
	@FXML
	private Label lblIMDBRating;
	@FXML
	private Button viewFilms;

	@FXML
	private Text txtdescription;
	private String uploadedImagePath = "";
	private final DBConnect dbConnect = new DBConnect();

	private int currentFilmId;

	/**
	 * Sets the film ID and loads its details.
	 *
	 * @param imageId The ID of the film to load.
	 */
	public void setFilmId(int imageId) {
		currentFilmId = imageId;
		loadFilmDetails(currentFilmId);
	}

	private void loadFilmDetails(int imageId) {
		String query = "SELECT image_data, trailer, title, description, date_from, date_to, times, age_rating, imdb_rating FROM films WHERE id = ?";

		try (Connection connection = dbConnect.connect();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, imageId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				// Load image
				byte[] imageData = resultSet.getBytes("image_data");
				if (imageData != null) {
					ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
					Image image = new Image(bis);
					uploadedFilmPoster.setImage(image);
				} else {
					System.out.println("No image data available for this film.");
				}

				// Load text data
				titleField.setText(resultSet.getString("title"));
				txtTitle.setText(resultSet.getString("title"));
				descriptionArea.setText(resultSet.getString("description"));
				txtdescription.setText(resultSet.getString("description"));
				trailerField.setText(resultSet.getString("trailer"));

				// Load date pickers
				LocalDate fromDateValue = resultSet.getDate("date_from").toLocalDate();
				LocalDate toDateValue = resultSet.getDate("date_to").toLocalDate();
				fromDate.setValue(fromDateValue);
				toDate.setValue(toDateValue);

				// Load time choices
				String[] times = resultSet.getString("times").split(",");
				if (times.length > 0) {
					timeChoice1.setValue(times[0]);
				}
				if (times.length > 1) {
					timeChoice2.setValue(times[1]);
				}
				if (times.length > 2) {
					timeChoice3.setValue(times[2]);
				}

				// Load age rating and IMDB rating
				ageRatingChoice.setValue(resultSet.getString("age_rating"));
				imdbRatingField.setText(resultSet.getString("imdb_rating"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving film details from database: " + e.getMessage());
		}
	}

	@FXML
	public void homeButtonOnAction(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/EmployeeHome.fxml"));
			Stage stage = (Stage) homeBtn.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			showAlert("Error", "Error loading the home page: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Show an alert in case of errors
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	public void viewFilmsButtonOnAction(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/ViewFilms.fxml"));
			Stage stage = (Stage) viewFilms.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			showAlert("Error", "Error loading the home page: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		// Set default image
		try {
			Image defaultImage = new Image(getClass().getResourceAsStream("/images/defaultFilmPoster.png"));
			uploadedFilmPoster.setImage(defaultImage);
		} catch (Exception e) {
			System.err.println("Could not load default image: " + e.getMessage());
		}

		// Initialize event handlers
		uploadImageButton.setOnAction(this::handleUploadImage);

		// Initialize choice boxes
		initializeChoiceBoxes();
	}

	private void initializeChoiceBoxes() {
		// Initialize time choices
		String[] times = { "10:00", "13:00", "16:00", "19:00", "22:00" };
		timeChoice1.getItems().addAll(times);
		timeChoice2.getItems().addAll(times);
		timeChoice3.getItems().addAll(times);

		// Initialize age ratings
		String[] ratings = { "U", "PG", "12A", "15", "18" };
		ageRatingChoice.getItems().addAll(ratings);
	}

	@FXML
	void handleUploadImage(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));

		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			try {
				// Create image for display
				Image image = new Image(selectedFile.toURI().toString());

				// Validate image was loaded successfully
				if (image.isError()) {
					throw new IOException("Failed to load image: " + image.getException().getMessage());
				}

				uploadedFilmPoster.setImage(image);
				uploadedImagePath = selectedFile.getAbsolutePath();
				showAlert("Success", "Image uploaded successfully");
			} catch (Exception e) {
				showAlert("Error", "Failed to load image: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void updateFilmDetails(KeyEvent e) {
		// Determine the source of the event
		switch (((Node) e.getSource()).getId()) {
		case "titleField": // If the title field is updated
			txtTitle.setText(titleField.getText());
			break;
		case "descriptionArea": // If the description area is updated
			txtdescription.setText(descriptionArea.getText());
			break;
		case "imdbRatingField": // If the imdbRatingField is updated
			lblIMDBRating.setText(imdbRatingField.getText());
			break;
		}
	}

	@FXML
	public void updateDateTimeAge(ActionEvent e) {
		try {
			switch (((Node) e.getSource()).getId()) {
			case "fromDate":
				LocalDate startDate = fromDate.getValue();
				if (startDate != null) {
					String startDateFormatted = startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					lblFrom.setText(startDateFormatted);
				} else {
					lblFrom.setText("Invalid start date");
				}
				break;
			case "toDate":
				LocalDate endDate = toDate.getValue();
				if (endDate != null) {
					String endDateFormatted = endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
					lblTo.setText(endDateFormatted);
				} else {
					lblTo.setText("Invalid end date");
				}
				break;
			case "timeChoice1":
				if (timeChoice1.getValue() != null) {
					lblTime1.setText(timeChoice1.getValue());
				} else {
					lblTime1.setText("Invalid time selection");
				}
				break;
			case "timeChoice2":
				if (timeChoice2.getValue() != null) {
					lblTime2.setText(timeChoice2.getValue());
				} else {
					lblTime2.setText("Invalid time selection");
				}
				break;
			case "timeChoice3":
				if (timeChoice3.getValue() != null) {
					lblTime3.setText(timeChoice3.getValue());
				} else {
					lblTime3.setText("Invalid time selection");
				}
				break;
			case "ageRatingChoice":
				if (ageRatingChoice.getValue() != null) {
					lblAgeRating.setText(ageRatingChoice.getValue());
				} else {
					lblAgeRating.setText("Invalid age rating selection");
				}
				break;
			default:
				System.out.println("Unknown control triggered the event");
				break;
			}
		} catch (Exception ex) {
			// Handle any unexpected exceptions
			ex.printStackTrace();
			// Optionally show an alert to the user
			showErrorAlert("An error occurred while updating the date and time.");
		}
	}

	private void showErrorAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Error occurred");
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	void handleUpdateFilm(ActionEvent event) {
		// Validate inputs before updating
		if (!validateInputs()) {
			return;
		}

		// Collect all updated values
		String title = titleField.getText().trim();
		String description = descriptionArea.getText().trim();
		String trailer = trailerField.getText().trim();
		LocalDate from = fromDate.getValue();
		LocalDate to = toDate.getValue();

		// Handle time selections with null check
		List<String> selectedTimes = new ArrayList<>();
		if (timeChoice1.getValue() != null)
			selectedTimes.add(timeChoice1.getValue());
		if (timeChoice2.getValue() != null)
			selectedTimes.add(timeChoice2.getValue());
		if (timeChoice3.getValue() != null)
			selectedTimes.add(timeChoice3.getValue());
		String times = String.join(",", selectedTimes);

		String ageRating = ageRatingChoice.getValue();
		String imdbRating = imdbRatingField.getText().trim();

		try (Connection connection = dbConnect.connect()) {
			// Prepare update query
			String updateQuery = "UPDATE Films SET " + "trailer = ?, " + "title = ?, " + "description = ?, "
					+ "date_from = ?, " + "date_to = ?, " + "times = ?, " + "age_rating = ?, " + "imdb_rating = ? "
					+ (uploadedImagePath.isEmpty() ? "" : ", image_data = ? ") + "WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
				int paramIndex = 1;
				preparedStatement.setString(paramIndex++, trailer);
				preparedStatement.setString(paramIndex++, title);
				preparedStatement.setString(paramIndex++, description);
				preparedStatement.setDate(paramIndex++, java.sql.Date.valueOf(from));
				preparedStatement.setDate(paramIndex++, java.sql.Date.valueOf(to));
				preparedStatement.setString(paramIndex++, times);
				preparedStatement.setString(paramIndex++, ageRating);
				preparedStatement.setString(paramIndex++, imdbRating);

				// Add image if a new image was uploaded
				if (!uploadedImagePath.isEmpty()) {
					FileInputStream fis = new FileInputStream(uploadedImagePath);
					preparedStatement.setBinaryStream(paramIndex++, fis);
				}

				// Set the film ID for the WHERE clause
				preparedStatement.setInt(paramIndex, currentFilmId);

				// Execute the update
				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					// Update UI elements to reflect changes
					updateUIElements(title, description, trailer, from, to, selectedTimes, ageRating, imdbRating);

					showAlert(Alert.AlertType.INFORMATION, "Success", "Film details updated successfully!");
				} else {
					showAlert(Alert.AlertType.ERROR, "Error", "No film was updated. Please check the film ID.");
				}
			}
		} catch (SQLException | IOException e) {
			showAlert(Alert.AlertType.ERROR, "Error", "Error updating film details: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Validates all input fields before update
	 * 
	 * @return boolean indicating if all inputs are valid
	 */
	private boolean validateInputs() {
		// Clear previous error styling
		clearValidationErrors();

		boolean isValid = true;

		// Title validation
		if (titleField.getText().trim().isEmpty()) {
			markFieldAsError(titleField);
			isValid = false;
		}

		// Description validation
		if (descriptionArea.getText().trim().isEmpty()) {
			markFieldAsError(descriptionArea);
			isValid = false;
		}

		// Trailer validation
		if (trailerField.getText().trim().isEmpty()) {
			markFieldAsError(trailerField);
			isValid = false;
		}

		// Date validations
		if (fromDate.getValue() == null) {
			markFieldAsError(fromDate);
			isValid = false;
		}

		if (toDate.getValue() == null) {
			markFieldAsError(toDate);
			isValid = false;
		}

		// Validate date range
		if (fromDate.getValue() != null && toDate.getValue() != null) {
			if (fromDate.getValue().isAfter(toDate.getValue())) {
				markFieldAsError(fromDate);
				markFieldAsError(toDate);
				showAlert(Alert.AlertType.ERROR, "Validation Error", "From date must be before or equal to To date.");
				isValid = false;
			}
		}

		// Time choices validation
		if (timeChoice1.getValue() == null) {
			markFieldAsError(timeChoice1);
			isValid = false;
		}

		// Age rating validation
		if (ageRatingChoice.getValue() == null) {
			markFieldAsError(ageRatingChoice);
			isValid = false;
		}

		// IMDB Rating validation
		if (imdbRatingField.getText().trim().isEmpty()) {
			markFieldAsError(imdbRatingField);
			isValid = false;
		}

		// Image validation (only if no previous image exists)
		if (uploadedImagePath.isEmpty() && uploadedFilmPoster.getImage() == null) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Please upload an image.");
			isValid = false;
		}

		if (!isValid) {
			showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all required fields correctly.");
		}

		return isValid;
	}

	/**
	 * Updates UI elements to reflect the changes after successful update
	 */
	private void updateUIElements(String title, String description, String trailer, LocalDate from, LocalDate to,
			List<String> times, String ageRating, String imdbRating) {
		// Update text displays
		txtTitle.setText(title);
		txtdescription.setText(description);

		// If a new image was uploaded, update the ImageView
		if (!uploadedImagePath.isEmpty()) {
			try {
				Image newImage = new Image(new FileInputStream(uploadedImagePath));
				uploadedFilmPoster.setImage(newImage);
			} catch (IOException e) {
				System.err.println("Error loading new image: " + e.getMessage());
			}
		}

		// Add any additional UI update logic as needed
	}

	/**
	 * Marks a field with error styling
	 * 
	 * @param node The JavaFX control to mark as error
	 */
	private void markFieldAsError(Control node) {
		node.getStyleClass().add("error-field");
	}

	/**
	 * Clears validation error styling from fields
	 */
	private void clearValidationErrors() {
		// Remove error styling from all input fields
		titleField.getStyleClass().remove("error-field");
		descriptionArea.getStyleClass().remove("error-field");
		trailerField.getStyleClass().remove("error-field");
		fromDate.getStyleClass().remove("error-field");
		toDate.getStyleClass().remove("error-field");
		timeChoice1.getStyleClass().remove("error-field");
		timeChoice2.getStyleClass().remove("error-field");
		timeChoice3.getStyleClass().remove("error-field");
		ageRatingChoice.getStyleClass().remove("error-field");
		imdbRatingField.getStyleClass().remove("error-field");
	}

	/**
	 * Overloaded method to show alerts with different types
	 * 
	 * @param alertType Type of alert
	 * @param title     Title of the alert
	 * @param message   Message to display
	 */
	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	public void handleHomeButtonAction() {
		// Logic to handle back button
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/ViewFilms.fxml"));

			// Get the current stage
			Stage stage = (Stage) homeBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
