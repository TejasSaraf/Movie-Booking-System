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

		if (exists) {
			System.out.println("Table '" + tableName + "' already exists!");
		} else {
			System.out.println("Table '" + tableName + "' does not exist!");
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
					+ "Password VARCHAR(45) NOT NULL, " + "emailaddress VARCHAR(45) DEFAULT NULL, "
					+ "dateofbirth DATE DEFAULT NULL, " + "address VARCHAR(45) DEFAULT NULL, "
					+ "phoneno VARCHAR(45) DEFAULT NULL, " + "PRIMARY KEY (idUserAccounts), "
					+ "UNIQUE KEY Username_UNIQUE (Username)) ";

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
					+ "PRIMARY KEY (idadminAccounts), " + "UNIQUE KEY Username_UNIQUE (Username)) ";

			stmt.executeUpdate(createTableSQL);
			System.out.println("Created table successfully...");

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void createTableForFilms(String tableName) {
		try {
			stmt = dbConnection.createStatement();
			System.out.println("Creating table " + tableName + " in the database...");

			// Correct table creation query
			String createTableSQL = "CREATE TABLE " + tableName + " (" + "id INT NOT NULL AUTO_INCREMENT, "
					+ "image_data LONGBLOB, " + "trailer VARCHAR(255) DEFAULT NULL, "
					+ "title VARCHAR(255) DEFAULT NULL, " + "description TEXT, " + "date_from DATE DEFAULT NULL, "
					+ "date_to DATE DEFAULT NULL, " + "times VARCHAR(255) DEFAULT NULL, "
					+ "age_rating VARCHAR(10) DEFAULT NULL, " + "imdb_rating VARCHAR(10) DEFAULT NULL, "
					+ "PRIMARY KEY (id)) ";

			stmt.executeUpdate(createTableSQL);
			System.out.println("Table " + tableName + " created successfully...");

		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	public void createTableForBookings(String tableName) {
		try {
			stmt = dbConnection.createStatement();
			System.out.println("Creating table " + tableName + " in the database...");

			// Correct table creation query
			String createTableSQL = "CREATE TABLE " + tableName + " (" + "id INT NOT NULL AUTO_INCREMENT, "
					+ "firstname VARCHAR(100) NOT NULL, " + "lastname VARCHAR(100) NOT NULL, "
					+ "email VARCHAR(255) NOT NULL, " + "film_title VARCHAR(255) NOT NULL, "
					+ "screen_num VARCHAR(50) NOT NULL, " + "date DATE NOT NULL, " + "time VARCHAR(20) NOT NULL, "
					+ "adult_tickets INT NOT NULL DEFAULT 0, " + "child_tickets INT NOT NULL DEFAULT 0, "
					+ "senior_tickets INT NOT NULL DEFAULT 0, " + "total_price DECIMAL(10,2) NOT NULL, "
					+ "is_vip TINYINT(1) NOT NULL DEFAULT 0, " + "PRIMARY KEY (id)) ";

			stmt.executeUpdate(createTableSQL);
			System.out.println("Table " + tableName + " created successfully...");

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

	public void insertUser(String tableName, String firstName, String lastName, String username, String password,
			String emailaddress, String dateofbirth, String address, String phoneno) {
		// First, check if a user with the same username already exists
		String checkUserSQL = "SELECT COUNT(*) FROM " + tableName + " WHERE Username = ?";
		try (PreparedStatement checkStmt = dbConnection.prepareStatement(checkUserSQL)) {
			checkStmt.setString(1, username);
			ResultSet rs = checkStmt.executeQuery();
			rs.next();
			int userCount = rs.getInt(1);

			// If the user already exists, print an error and return
			if (userCount > 0) {
				System.err.println("Error: A user with the username '" + username + "' already exists in " + tableName);
				return; // Prevent inserting a duplicate user
			}

			// Proceed to insert the new user
			String insertSQL = "INSERT INTO " + tableName
					+ " (Firstname, Lastname, Username, Password, emailaddress, dateofbirth, address, phoneno) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement pstmt = dbConnection.prepareStatement(insertSQL)) {
				pstmt.setString(1, firstName);
				pstmt.setString(2, lastName);
				pstmt.setString(3, username);
				pstmt.setString(4, hashPassword(password)); // Hash the password before inserting
				pstmt.setString(5, emailaddress);
				pstmt.setString(6, dateofbirth);
				pstmt.setString(7, address);
				pstmt.setString(8, phoneno);

				int rowsInserted = pstmt.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("User details inserted successfully into " + tableName);
				}
			} catch (SQLException se) {
				se.printStackTrace(); // Log other SQL exceptions
			}
		} catch (SQLException se) {
			se.printStackTrace();
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
		insertUser(tableName, "Jake", "Paul", "Jake", "#Jake@123", "jake@gmail.com", "2000-10-01", "Chicago",
				"3125598767");
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

	public void checkFilmsTable() {
		String tableName = "films";

		// Check if table exists
		if (!isTableExists(tableName)) {
			// If table doesn't exist, create it
			createTableForFilms(tableName);
		}
	}

	public void checkBookingsTable() {
		String tableName = "bookings";

		// Check if table exists
		if (!isTableExists(tableName)) {
			// If table doesn't exist, create it
			createTableForBookings(tableName);
		}
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
