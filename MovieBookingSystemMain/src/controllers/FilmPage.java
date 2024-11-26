package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import models.DBConnect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmPage {
	@FXML
	private ImageView filmImageView;
	@FXML
	private Label lblTitle;
	@FXML
	private Text txtdescription;
	@FXML
	private Label lblFrom;
	@FXML
	private Label lblTo;
	@FXML
	private Label lblAgeRating;
	@FXML
	private Label lblImdbRating;
	@FXML
	private Label lblTimes;
	@FXML
	private Button btnDelete;

	private final DBConnect dbConnect = new DBConnect();
	private int currentFilmId;

	public void setImageId(int imageId) {
		currentFilmId = imageId;
		loadFilmDetails(imageId);
	}

	private void loadFilmDetails(int imageId) {
		String query = "SELECT image_data, title, description, date_from, date_to, "
				+ "age_rating, imdb_rating, times FROM films WHERE id = ?";

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
					filmImageView.setImage(image);
				}

				// Load text data
				lblTitle.setText(resultSet.getString("title"));
				txtdescription.setText(resultSet.getString("description"));
				lblFrom.setText(resultSet.getDate("date_from").toString());
				lblTo.setText(resultSet.getDate("date_to").toString());
				lblAgeRating.setText(resultSet.getString("age_rating"));
				lblImdbRating.setText(resultSet.getString("imdb_rating"));
				lblTimes.setText(resultSet.getString("times"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving film details from database: " + e.getMessage());
		}
	}

	@FXML
	private void handleDeleteFilm() {
		if (currentFilmId == 0) {
			showAlert(Alert.AlertType.ERROR, "No film selected", "Please select a film to delete.");
			return;
		}

		String deleteQuery = "DELETE FROM films WHERE id = ?";

		try (Connection connection = dbConnect.connect();
				PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

			statement.setInt(1, currentFilmId);

			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Film Deleted",
						"The selected film has been deleted successfully.");
				redirectToViewFilms();
			} else {
				showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete the film. Please try again.");
			}

		} catch (SQLException | IOException e) {
			e.printStackTrace();
			System.out.println("Error deleting film from database: " + e.getMessage());
		}
	}

	private void redirectToViewFilms() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ViewFilms.fxml"));
		Scene scene = new Scene(loader.load());

		// Get the current stage and set the new scene
		Stage stage = (Stage) btnDelete.getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType, message, ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.showAndWait();
	}
}
