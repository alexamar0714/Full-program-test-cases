package com.motour.recommender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TravelDestination {
	private String Attribute;

	public void TravelDestination(int id) throws Exception {
		Connection conn;
		Statement stmt = null;
		String Property = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.err.print("ClassNotFoundException");
		}

		try {
			String jdbcUrl = "jdbc:mysql://localhost:3306/tourofall2?autoReconnect=true&useSSL=false";
			String userId = "root";
			String userPass = "465651";

			conn = DriverManager.getConnection(jdbcUrl, userId, userPass);
			stmt = conn.createStatement();

			String sql = "select property from place where Content_Id = " + id + ";";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Property = rs.getString("property");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException" + e.getMessage());
		}
		Attribute = Property;
	}

	public String getAttribute() {
		return this.Attribute;
	}
}
