package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SeatBooking {

	@FXML
	private Button backBtn, confirmationBtn;
	@FXML
	private Button A1, A2, A3, A4, A5, A6, A7, A8, B1, B2, B3, B4, B5, B6, B7, B8, C1, C2, C3, C4, C5, C6, C7, C8;

	@FXML
	private Text filmName, totalTickets, totalPrice;
	@FXML
	private Label selectedSeats;

	private Button[] seats;
	private static boolean[] bookings = new boolean[24];
	private int maxSeats;
	private int numberOfSeats = 0;
	public static List<String> selectedSeatList = new ArrayList<>();

	public static String userSeats = "";

	public static boolean seatsSelected = false;
	
	private int currentFilmId;

	/**
	 * Sets the film ID and loads its details.
	 *
	 * @param imageId The ID of the film to load.
	 */
	public void setFilmId(int imageId) {
		currentFilmId = imageId;
	}

	@FXML
	private void initialize() {
		// Initialize seats array with both button and its name
		seats = new Button[] { A1, A2, A3, A4, A5, A6, A7, A8, B1, B2, B3, B4, B5, B6, B7, B8, C1, C2, C3, C4, C5, C6,
				C7, C8 };

		// Add event handlers to each seat button
		for (int i = 0; i < seats.length; i++) {
			final int seatIndex = i;
			seats[i].setOnAction(event -> handleSeatSelection(seatIndex));
			updateSeatStyle(i);
		}

		// Display initial data
		filmName.setText(TicketBookings.selectedFilmTitleStr); // Replace with actual film name
		totalTickets.setText(String.valueOf(TicketBookings.totalTicketsCount));
		totalPrice.setText(String.format("$%.2f", TicketBookings.total));
		selectedSeats.setText("None");

		// Fetch the total number of tickets allowed
		maxSeats = TicketBookings.adultTickets + TicketBookings.childTickets + TicketBookings.seniorTickets;
	}

	private void handleSeatSelection(int seatIndex) {
		// Use the index to get the correct seat name
		String[] seatNames = { "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "B1", "B2", "B3", "B4", "B5", "B6", "B7",
				"B8", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8" };

		String seatText = seatNames[seatIndex];

		if (bookings[seatIndex]) {
			// Seat already booked
			return;
		}

		// If the seat is already selected, remove it
		if (selectedSeatList.contains(seatText)) {
			selectedSeatList.remove(seatText);
			numberOfSeats--;
		} else {
			// If maximum seats not reached, add the seat
			if (numberOfSeats < maxSeats) {
				selectedSeatList.add(seatText);
				numberOfSeats++;
			} else {
				// Maximum seats selected, show warning
				System.out.println("Maximum seats selected!");
				return;
			}
		}

		// Update seat style and UI
		updateSeatStyle(seatIndex);
		updateSelectedSeats();
	}

	private void updateSeatStyle(int seatIndex) {
		String[] seatNames = { "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "B1", "B2", "B3", "B4", "B5", "B6", "B7",
				"B8", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8" };

		String seatText = seatNames[seatIndex];

		if (bookings[seatIndex]) {
			// Booked seats
			seats[seatIndex].setStyle("-fx-background-color: #e40606;"); // Red
		} else if (selectedSeatList.contains(seatText)) {
			// Selected seats
			seats[seatIndex].setStyle("-fx-background-color: #23b33b;"); // Green
		} else {
			// Available seats
			seats[seatIndex].setStyle("-fx-background-color: #edf0f4;"); // Default
		}
		seats[seatIndex].setEffect(new DropShadow());
	}

	private void updateSelectedSeats() {
		// Sort the seats
		selectedSeatList.sort((seat1, seat2) -> {
			try {
				// Extract row (letter) and column (number) for both seats
				char row1 = seat1.charAt(0);
				char row2 = seat2.charAt(0);
				int col1 = Integer.parseInt(seat1.substring(1));
				int col2 = Integer.parseInt(seat2.substring(1));

				// Compare rows first; if rows are the same, compare columns
				if (row1 != row2) {
					return Character.compare(row1, row2);
				}
				return Integer.compare(col1, col2);
			} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
				System.err.println("Invalid seat format: " + seat1 + " or " + seat2);
				return 0; // Keep the list intact for debugging
			}
		});

		// Join the sorted seats into a string with proper formatting
		userSeats = String.join(", ", selectedSeatList);

		// Update the label to show the selected seats
		selectedSeats.setText(selectedSeatList.isEmpty() ? "None" : userSeats);
	}

	@FXML
	private void handleConfirmation() {
		if (numberOfSeats == 0) {
			System.out.println("No seats selected!");
			return;
		}

		// Mark seats as booked
		for (int i = 0; i < seats.length; i++) {
			String[] seatNames = { "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "B1", "B2", "B3", "B4", "B5", "B6",
					"B7", "B8", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8" };

			if (selectedSeatList.contains(seatNames[i])) {
				bookings[i] = true;
			}
		}

		seatsSelected = true;
		System.out.println("Seats confirmed: " + userSeats);
		// Navigate to confirmation or next screen
		try {
		    // Load the SeatBooking.fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ConfirmationPage.fxml"));
			Parent root = loader.load();

		    // Get the current stage
		 // Get the controller from the loaded FXML
		 			Confirmation confirmationPageController = loader.getController();

		 			// Pass the imageId to the FilmPage controller
		 			confirmationPageController.setFilmId(currentFilmId);

		    // Set the new scene
		 			Stage stage = (Stage) confirmationBtn.getScene().getWindow();
		    stage.setScene(new Scene(root));
		    stage.show();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	@FXML
	private void handleBack() {
		// Logic to handle back button
		try {
			// Load the Login.fxml file
			Parent root = FXMLLoader.load(getClass().getResource("/views/TicketBookings.fxml"));

			// Get the current stage
			Stage stage = (Stage) backBtn.getScene().getWindow();

			// Set the new scene
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}