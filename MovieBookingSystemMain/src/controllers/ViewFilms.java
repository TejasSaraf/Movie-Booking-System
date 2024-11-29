package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import models.DBConnect;
import models.UserSession;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.IOException;

public class ViewFilms {

	@FXML
	private ScrollPane scrollPane;
	@FXML
	private TilePane tilePane;
	@FXML
	private Button btnLogout;

	@FXML
	public void logoutButtonOnAction(ActionEvent e) {
	    UserSession.getInstance().clearSession();
	    try {
	        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
	        javafx.scene.Scene scene = new javafx.scene.Scene(root);
	        javafx.stage.Stage stage = (javafx.stage.Stage) btnLogout.getScene().getWindow();
	        stage.setScene(scene);
	        stage.show();
	    } catch (Exception ex) {
	        ex.printStackTrace();
//	        lblStatus.setText("Error navigating to login page.");
	    }
	}


	@FXML
	public void initialize() {
		loadImages();
	}

	private final DBConnect dbConnect = new DBConnect();
	private static final String RETRIEVE_IMAGES_QUERY = "SELECT id, image_data FROM films";

	public void loadImages() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = dbConnect.connect();
			statement = connection.prepareStatement(RETRIEVE_IMAGES_QUERY);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				byte[] imageData = resultSet.getBytes("image_data");
				int imageId = resultSet.getInt("id"); // Get image id

				if (imageData != null) {
					ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
					Image image = new Image(bis);
					ImageView imageView = new ImageView(image);
					imageView.setFitWidth(200);
					imageView.setFitHeight(150);
					imageView.setPreserveRatio(true);

					// Set onMouseClicked event handler
					imageView.setOnMouseClicked((MouseEvent event) -> {
						openFilmPage(imageId); // Pass image id to FilmPage
					});

					tilePane.getChildren().add(imageView);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error retrieving images from database.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error loading images from database.");
		} finally {
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void openFilmPage(int imageId) {
		try {
			// Load the FilmPage.fxml file
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/FilmPage.fxml"));
			Parent root = loader.load();

			// Get the controller from the loaded FXML
			FilmPage filmPageController = loader.getController();

			// Pass the imageId to the FilmPage controller
			filmPageController.setImageId(imageId);

			// Create a new scene and set it to the current stage
			Scene scene = new Scene(root);
			Stage stage = (Stage) tilePane.getScene().getWindow(); // Get the current stage
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error loading FilmPage.fxml.");
		}
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
