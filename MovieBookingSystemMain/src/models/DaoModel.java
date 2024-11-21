package models;

import java.sql.*;
import java.security.MessageDigest;
import java.util.Base64;

public class DaoModel {
	// Declare DB objects
	private DBConnect conn = null;
	private Statement stmt = null;
	private Connection dbConnection = null;

	// Constructor to create a DBConnect object instance and open a connection
	public DaoModel() {
		conn = new DBConnect();
		try {
			// Open a single database connection here
			dbConnection = conn.connect();
			System.out.println("Connecting to database...");
			System.out.println("Connected to database successfully...");
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	// Method to check if the useraccounts table exists
	public boolean isTableExists(String tableName) {
		boolean exists = false;
		try {
			DatabaseMetaData dbMetaData = dbConnection.getMetaData();
			ResultSet rs = dbMetaData.getTables(null, null, tableName, new String[] { "TABLE" });
			exists = rs.next();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return exists;
	}

	// Method to create the useraccounts table if it doesn't exist
	public void createTable(String tableName) {
		try {
			stmt = dbConnection.createStatement();
			System.out.println("Creating table " + tableName + " in the database...");

			// Create the table
			String createTableSQL = "CREATE TABLE " + tableName + " ("
					+ "idUserAccounts INT UNSIGNED NOT NULL AUTO_INCREMENT, " + "Firstname VARCHAR(45) NOT NULL, "
					+ "Lastname VARCHAR(45) NOT NULL, " + "Username VARCHAR(45) NOT NULL, "
					+ "Password VARCHAR(45) NOT NULL, " + "PRIMARY KEY (idUserAccounts), "
					+ "UNIQUE KEY Username_UNIQUE (Username)) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";

			stmt.executeUpdate(createTableSQL);
			System.out.println("Created table successfully...");

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void createTableForAdmin(String tableName) {
		try {
			stmt = dbConnection.createStatement();
			System.out.println("Creating table " + tableName + " in the database...");

			// Correct table creation query
			String createTableSQL = "CREATE TABLE " + tableName + " ("
					+ "idadminAccounts INT UNSIGNED NOT NULL AUTO_INCREMENT, " + "Firstname VARCHAR(45) NOT NULL, "
					+ "Lastname VARCHAR(45) NOT NULL, " + "Username VARCHAR(45) NOT NULL, "
					+ "Password VARCHAR(45) NOT NULL, " // Fixed column case
					+ "PRIMARY KEY (idadminAccounts), " + "UNIQUE KEY Username_UNIQUE (Username)) "
					+ "ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";

			stmt.executeUpdate(createTableSQL);
			System.out.println("Created table successfully...");

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	private String hashPassword(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = md.digest(password.getBytes());
			return Base64.getEncoder().encodeToString(hashedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void insertUser(String tableName, String firstName, String lastName, String username, String password) {
		String insertSQL = "INSERT INTO " + tableName
				+ " (Firstname, Lastname, Username, Password) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = dbConnection.prepareStatement(insertSQL)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, username);
			pstmt.setString(4, hashPassword(password)); // Hash the password before inserting

			int rowsInserted = pstmt.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("User details inserted successfully into " + tableName);
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			System.err.println("Error: A user with the username '" + username + "' already exists in " + tableName);
			// Optionally, log the error or rethrow it if needed
		} catch (SQLException se) {
			se.printStackTrace(); // Log other SQL exceptions
		}
	}

	public void insertUserForAdmin(String tableName, String firstName, String lastName, String username,
			String password) {
		String insertSQL = "INSERT INTO " + tableName
				+ " (Firstname, Lastname, Username, Password) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = dbConnection.prepareStatement(insertSQL)) {
			pstmt.setString(1, firstName);
			pstmt.setString(2, lastName);
			pstmt.setString(3, username);
			pstmt.setString(4, hashPassword(password)); // Hash the password before inserting

			int rowsInserted = pstmt.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("Admin details inserted successfully into " + tableName);
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			System.err.println("Error: A user with the username '" + username + "' already exists in " + tableName);
			// Optionally, log the error or rethrow it if needed
		} catch (SQLException se) {
			se.printStackTrace(); // Log other SQL exceptions
		}
	}

	// Method to check table existence, create if not exists, and insert user
	// details
	public void checkAndInsertUser() {
		String tableName = "useraccounts";

		// Check if table exists
		if (!isTableExists(tableName)) {
			// If table doesn't exist, create it
			createTable(tableName);
		}

		// Insert user details into the table
		insertUser(tableName, "Sumit", "Padwal", "Sumit", "#Sumit@123");
	}

	public void checkAndInsertUserForAdmin() {
		String tableName = "adminaccounts";

		// Check if table exists
		if (!isTableExists(tableName)) {
			// If table doesn't exist, create it
			createTableForAdmin(tableName);
		}

		// Insert user details into the table
		insertUserForAdmin(tableName, "Admin", "System", "Admin", "#Admin@123");
	}

	// Close the database connection
	public void closeConnection() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
				System.out.println("Database connection closed.");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
