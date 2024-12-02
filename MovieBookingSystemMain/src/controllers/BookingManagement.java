package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Booking;
import models.DBConnect;

public class BookingManagement implements Initializable {
	@FXML
	private TableView<Booking> bookingsTableView;

	@FXML
	private TableColumn<Booking, Integer> idColumn;

	@FXML
	private TableColumn<Booking, String> firstnameColumn;

	@FXML
	private TableColumn<Booking, String> lastnameColumn;

	@FXML
	private TableColumn<Booking, String> emailColumn;

	@FXML
	private TableColumn<Booking, String> filmTitleColumn;

	@FXML
	private TableColumn<Booking, String> screenNumColumn;

	@FXML
	private TableColumn<Booking, String> dateColumn;

	@FXML
	private TableColumn<Booking, String> timeColumn;

	@FXML
	private TableColumn<Booking, Integer> adultTicketsColumn;

	@FXML
	private TableColumn<Booking, Integer> childTicketsColumn;

	@FXML
	private TableColumn<Booking, Integer> seniorTicketsColumn;

	@FXML
	private TableColumn<Booking, Double> totalPriceColumn;

	@FXML
	private TableColumn<Booking, Boolean> isVipColumn;

	@FXML
	private Button homeBtn;

	private final DBConnect dbConnect = new DBConnect();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set up the columns
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
		firstnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstname"));
		lastnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastname"));
		emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
		filmTitleColumn.setCellValueFactory(new PropertyValueFactory<>("filmTitle"));
		screenNumColumn.setCellValueFactory(new PropertyValueFactory<>("screenNum"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
		adultTicketsColumn.setCellValueFactory(new PropertyValueFactory<>("adultTickets"));
		childTicketsColumn.setCellValueFactory(new PropertyValueFactory<>("childTickets"));
		seniorTicketsColumn.setCellValueFactory(new PropertyValueFactory<>("seniorTickets"));
		totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
		isVipColumn.setCellValueFactory(new PropertyValueFactory<>("isVip"));

		// Load bookings
		loadBookings();
	}

	private void loadBookings() {
		ObservableList<Booking> bookingsList = FXCollections.observableArrayList();

		String query = "SELECT * FROM bookings";

		try (Connection connection = dbConnect.connect();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			while (resultSet.next()) {
				Booking booking = new Booking(resultSet.getInt("id"), resultSet.getString("firstname"),
						resultSet.getString("lastname"), resultSet.getString("email"),
						resultSet.getString("film_title"), resultSet.getString("screen_num"),
						resultSet.getDate("date").toLocalDate(), resultSet.getString("time"),
						resultSet.getInt("adult_tickets"), resultSet.getInt("child_tickets"),
						resultSet.getInt("senior_tickets"), resultSet.getDouble("total_price"),
						resultSet.getBoolean("is_vip"));
				bookingsList.add(booking);
			}

			// Set the items to the TableView
			bookingsTableView.setItems(bookingsList);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving bookings from database: " + e.getMessage());
		}
	}

	@FXML
	private void handleHomeBtn() {
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