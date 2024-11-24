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
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.DBConnect;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javafx.scene.Node;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FilmManagement implements Initializable {

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
	private Button backBtn;
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
	private Text txtdescription;
	private String uploadedImagePath = "";
	private final DBConnect dbConnect = new DBConnect();

	@FXML
	public void backButtonOnAction(ActionEvent event) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/EmployeeHome.fxml"));
			Stage stage = (Stage) backBtn.getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			showAlert("Error", "Error loading the home page: " + e.getMessage());
			e.printStackTrace();
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
		addFilmButton.setOnAction(this::handleAddFilm);
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
	void handleAddFilm(ActionEvent event) {
		if (uploadedImagePath.isEmpty()) {
			showAlert("Validation Error", "Please upload an image first.");
			return;
		}

		String trailer = trailerField.getText();
		String title = titleField.getText();
		String description = descriptionArea.getText();
		String fromDateValue = fromDate.getValue() != null ? fromDate.getValue().toString() : "";
		String toDateValue = toDate.getValue() != null ? toDate.getValue().toString() : "";
		String time1 = timeChoice1.getValue() != null ? timeChoice1.getValue() : "";
		String time2 = timeChoice2.getValue() != null ? timeChoice2.getValue() : "";
		String time3 = timeChoice3.getValue() != null ? timeChoice3.getValue() : "";
		String ageRating = ageRatingChoice.getValue() != null ? ageRatingChoice.getValue() : "";
		String imdbRating = imdbRatingField.getText();

		// Validate inputs
		if (title.isEmpty() || trailer.isEmpty() || description.isEmpty() || fromDateValue.isEmpty()
				|| toDateValue.isEmpty() || imdbRating.isEmpty()) {
			showAlert("Validation Error", "All fields are required except Times.");
			return;
		}

		// Create a list of non-empty time values
		List<String> timesList = new ArrayList<>();
		if (!time1.isEmpty())
			timesList.add(time1);
		if (!time2.isEmpty())
			timesList.add(time2);
		if (!time3.isEmpty())
			timesList.add(time3);

		// Join the times with commas
		String times = String.join(", ", timesList);

		// Insert into database
		String insertQuery = "INSERT INTO Films (image_data, trailer, title, description, date_from, date_to, times, age_rating, imdb_rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

		try (Connection connectDB = dbConnect.connect();
				PreparedStatement preparedStatement = connectDB.prepareStatement(insertQuery);
				FileInputStream fis = new FileInputStream(uploadedImagePath)) {
			preparedStatement.setBinaryStream(1, fis); // Store image as BLOB
			preparedStatement.setString(2, trailer);
			preparedStatement.setString(3, title);
			preparedStatement.setString(4, description);
			preparedStatement.setString(5, fromDateValue);
			preparedStatement.setString(6, toDateValue);
			preparedStatement.setString(7, times);
			preparedStatement.setString(8, ageRating);
			preparedStatement.setString(9, imdbRating);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				showAlert("Success", "Film added successfully.");
				clearFields();
			} else {
				showAlert("Failure", "Failed to add film.");
			}
		} catch (SQLException | IOException e) {
			showAlert("Database Error", "Error inserting data into the database: " + e.getMessage());
			e.printStackTrace();
		}
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

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void clearFields() {
		trailerField.clear();
		titleField.clear();
		descriptionArea.clear();
		fromDate.setValue(null);
		toDate.setValue(null);
		timeChoice1.setValue(null);
		timeChoice2.setValue(null);
		timeChoice3.setValue(null);
		ageRatingChoice.setValue(null);
		imdbRatingField.clear();
		uploadedFilmPoster.setImage(new Image(getClass().getResourceAsStream("/images/defaultFilmPoster.png")));
		uploadedImagePath = "";
	}
}
