package controllers;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import models.DBConnect;
import models.UserSession;

public class Confirmation {

    @FXML
    Text firstname, lastName, email, datetime, screen, seats;
    
    @FXML
    Text selectedFilmTitle, adult, child, senior, isVip, total;
    
    @FXML
    ImageView selectedFilmPoster;
    
    @FXML
    Button homeBtn, downloadBtn;
    
    String selectedFilm = "", date = "", time = "";
    
    public static String name = "", finalDate = "", finalTime = "", vipConf = "";
    public static int min = 0;
    public static int max = 9999;
    public static int id = (int) Math.floor(Math.random() * (max - min + 1) + min);
    public static String bookingId = "CLUBH" + Integer.toString(id);
    
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
//				populateFilmTimes(resultSet.getString("times"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving film details from database: " + e.getMessage());
		}
	}

    // Initialize the confirmation screen with session and booking details
    @FXML
    public void initialize() {
    	
        // Retrieve user session details
        UserSession userSession = UserSession.getInstance();
        firstname.setText(userSession.getUsername());  // Assuming userSession stores the user's first name
        lastName.setText(userSession.getLastName());
        email.setText(userSession.getEmail()); // Replace with actual email retrieval logic
        
        // Set booking details
//        selectedFilmTitle.setText("Film Name");  // Set film title dynamically from booking data
//        selectedFilmPoster.setImage(new javafx.scene.image.Image("path_to_image"));  // Set the image dynamically
        
        // Populate the customer booking details (example values, replace with actual data)
        adult.setText(String.valueOf(TicketBookings.adultTickets));
        child.setText(String.valueOf(TicketBookings.childTickets));
        senior.setText(String.valueOf(TicketBookings.seniorTickets));
        isVip.setText(String.valueOf(TicketBookings.isVip));
        total.setText(String.format("$%.2f", TicketBookings.total));

        // Set date and time (from booking or database)
        datetime.setText(String.valueOf(TicketBookings.date) + " " + String.valueOf(TicketBookings.time));  // Ensure finalDate and finalTime are populated
        screen.setText(String.valueOf(TicketBookings.screenNum));  // Example screen number
        seats.setText(String.valueOf(SeatBooking.selectedSeatList));  // Example seats booked
    }

    // This method will be called when the 'Email' button is clicked
    @FXML
    public void emailConfirmation() {
        // Send confirmation email logic goes here
        // You can implement email logic using JavaMail API or other methods
        System.out.println("Email sent to " + email.getText());
    }

    // This method will be called when the 'Home' button is clicked
    @FXML
    public void goHome() {
        // Navigate back to the homepage or reset the session
        System.out.println("Redirecting to home...");
    }
}
