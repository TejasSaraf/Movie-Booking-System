package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import models.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.ByteArrayInputStream;

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

	private final DBConnect dbConnect = new DBConnect();

	public void setImageId(int imageId) {
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
				String title = resultSet.getString("title");
				lblTitle.setText(title != null ? title : "");

				String description = resultSet.getString("description");
				txtdescription.setText(description != null ? description : "");

				java.sql.Date dateFrom = resultSet.getDate("date_from");
				lblFrom.setText(dateFrom != null ? dateFrom.toString() : "");

				java.sql.Date dateTo = resultSet.getDate("date_to");
				lblTo.setText(dateTo != null ? dateTo.toString() : "");

				String ageRating = resultSet.getString("age_rating");
				lblAgeRating.setText(ageRating != null ? ageRating : "");

				String imdbRating = resultSet.getString("imdb_rating");
				lblImdbRating.setText(imdbRating != null ? imdbRating : "");

				String times = resultSet.getString("times");
				lblTimes.setText(times != null ? times : "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving film details from database: " + e.getMessage());
		}
	}
}