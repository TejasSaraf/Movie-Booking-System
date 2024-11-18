package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {

	static final String DB_URL = "jdbc:mysql://localhost:3306/movie";

	static final String USER = "root", PASS = "#Tejassaraf@123";

	public Connection connect() throws SQLException {

		return DriverManager.getConnection(DB_URL, USER, PASS);

	}
}
