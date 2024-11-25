package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.ByteArrayInputStream;

public class FilmPage {

    @FXML
    private ImageView filmImageView;

    private final DBConnect dbConnect = new DBConnect();

    public void setImageId(int imageId) {
        // Retrieve the film image by id and display it on the page
        loadFilmImage(imageId);
    }

    private void loadFilmImage(int imageId) {
        String query = "SELECT image_data FROM films WHERE id = ?";
        try (Connection connection = dbConnect.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, imageId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                byte[] imageData = resultSet.getBytes("image_data");
                if (imageData != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
                    Image image = new Image(bis);
                    filmImageView.setImage(image);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving film image from database.");
        }
    }
}
