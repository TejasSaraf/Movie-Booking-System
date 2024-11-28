package controllers;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.DBConnect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.IntStream;

public class TicketBookings {

	@FXML
	private Button backBtn, homeBtn, seatsBtn;
	@FXML
	private DatePicker selectedDate;
	@FXML
	private ComboBox<String> adultcombo, childcombo, seniorcombo, filmTimes;
	@FXML
	private Label selectedFilmTitle, totalPrice;
	@FXML
	private TextField screen;
	@FXML
	private CheckBox checkbox;
	@FXML
	private ImageView selectedFilmPoster;

	public static double total = 0.00;
	public static int adultTickets = 0, childTickets = 0, seniorTickets = 0;
	private double adultPrice = 50, childPrice = 20, seniorPrice = 40, vip = 5;
	public static boolean isVip = false;
	public static String screenNum = "", date = "", time = "";

	private final DBConnect dbConnect = new DBConnect();

	private int currentFilmId;

	/**
	 * Sets the film ID and loads its details.
	 *
	 * @param imageId The ID of the film to load.
	 */
	public void setFilmId(int imageId) {
		currentFilmId = imageId;
		loadFilmDetails(imageId);
	}

	/**
	 * Loads film details based on the film ID.
	 *
	 * @param imageId The ID of the film.
	 */
	private void loadFilmDetails(int imageId) {
		String query = "SELECT image_data, title, times FROM films WHERE id = ?";

		try (Connection connection = dbConnect.connect();
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, imageId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				byte[] imageData = resultSet.getBytes("image_data");
				if (imageData != null) {
					ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
					Image image = new Image(bis);
					selectedFilmPoster.setImage(image);
				} else {
					System.out.println("No image data available for this film.");
				}

				// Load text data
				selectedFilmTitle.setText(resultSet.getString("title"));
				populateFilmTimes(resultSet.getString("times"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving film details from database: " + e.getMessage());
		}
	}

	/**
	 * Populates the film times combo box.
	 *
	 * @param times A comma-separated list of film times.
	 */
	private void populateFilmTimes(String times) {
		if (times != null && !times.isEmpty()) {
			String[] timeSlots = times.split(",");
			filmTimes.getItems().addAll(timeSlots);
		}
	}

	@FXML
	public void initialize() {
		// Populate ticket quantity options
		IntStream.rangeClosed(0, 10).forEach(i -> {
			adultcombo.getItems().add(String.valueOf(i));
			childcombo.getItems().add(String.valueOf(i));
			seniorcombo.getItems().add(String.valueOf(i));
		});

		// Add listeners for ticket combos
		adultcombo.valueProperty().addListener(this::updateTotalPrice);
		childcombo.valueProperty().addListener(this::updateTotalPrice);
		seniorcombo.valueProperty().addListener(this::updateTotalPrice);

		// Add listener for VIP checkbox
		checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			isVip = newValue;
			updateTotalPrice(null, null, null);
		});

		// Add listener for date selection
		selectedDate.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				date = newValue.toString();
			}
		});

		// Add listener for film time selection
		filmTimes.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				time = newValue;
			}
		});

		// Add listener for screen input
		screen.textProperty().addListener((observable, oldValue, newValue) -> {
			screenNum = newValue;
		});
	}

	/**
	 * Updates the total price based on ticket selections and VIP status.
	 */
	private void updateTotalPrice(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		adultTickets = parseComboBoxValue(adultcombo);
		childTickets = parseComboBoxValue(childcombo);
		seniorTickets = parseComboBoxValue(seniorcombo);

		total = (adultTickets * adultPrice) + (childTickets * childPrice) + (seniorTickets * seniorPrice);
		if (isVip) {
			total += vip;
		}

		totalPrice.setText(String.format("$%.2f", total));
	}

	/**
	 * Parses the value of a ComboBox as an integer.
	 *
	 * @param comboBox The ComboBox to parse.
	 * @return The integer value, or 0 if no value is selected.
	 */
	private int parseComboBoxValue(ComboBox<String> comboBox) {
		String value = comboBox.getValue();
		return value != null ? Integer.parseInt(value) : 0;
	}

	@FXML
	public void handleBackButtonAction() {
		// Logic to handle back button
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/ViewFilms.fxml"));

			// Get the current stage
			Stage stage = (Stage) backBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void handleHomeButtonAction() {
		// Logic to handle home button
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

	@FXML
	public void handleSeatsButtonAction() {
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/SeatBooking.fxml"));

			// Get the current stage
			Stage stage = (Stage) seatsBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
