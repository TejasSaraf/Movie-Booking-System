package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.DBConnect;

public class UserSession {
    private static UserSession instance;
    private String username;
    private boolean isAdmin;
    
    // Add fields to store user details
    private String firstName;
    private String lastName;
    private String email;
    
    private final DBConnect dbConnect = new DBConnect();

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUsername(String username) {
        this.username = username;
        // When username is set, fetch user details
        fetchUserDetails();
    }

    public String getUsername() {
        return username;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    // New method to fetch user details from database
    private void fetchUserDetails() {
        String query = "SELECT Firstname, lastname, emailaddress FROM useraccounts WHERE username = ?";
        
        try (Connection connection = dbConnect.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, this.username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    this.firstName = resultSet.getString("Firstname");
                    this.lastName = resultSet.getString("lastname");
                    this.email = resultSet.getString("emailaddress");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving user details: " + e.getMessage());
        }
    }

    // Getter methods for user details
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void clearSession() {
        username = null;
        isAdmin = false;
        firstName = null;
        lastName = null;
        email = null;
    }
}