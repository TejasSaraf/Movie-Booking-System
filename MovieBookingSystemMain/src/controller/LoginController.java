package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import model.DBConnect;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private Label lblStatus;

    @FXML
    private TextField txtUserName;

    @FXML
    private TextField txtPassword;
    
    @FXML
    private Button loginButton;

    private final DBConnect dbConnect = new DBConnect(); // Instance of DBConnect

    public void loginButtonOnAction(ActionEvent e) {
        if (!txtUserName.getText().isBlank() && !txtPassword.getText().isBlank()) {
            validateLogin();
        } else {
            lblStatus.setText("Please Enter Username and Password");
        }
    }

    public void validateLogin() {
        try {
            // Get the database connection
            Connection connectDB = dbConnect.connect();

            // SQL query to verify user credentials
            String verifyLogin = "SELECT COUNT(1) FROM UserAccounts WHERE username = '" 
                + txtUserName.getText() + "' AND password = '" + txtPassword.getText() + "'";

            // Create statement and execute query
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (queryResult.next()) { // Check query result
                if (queryResult.getInt(1) == 1) {
                	Parent root = FXMLLoader.load(getClass().getResource("/view/Main.fxml"));
                    Scene scene = new Scene(root, 400, 400);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } else {
                    lblStatus.setText("Invalid Login!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("An error occurred.");
        }
    }
}
