package com.motour.recommender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class getPlaceURL {
	private String URL;
	
	public getPlaceURL(int id){
		Connection conn;
		Statement stmt = null;
		String url = null;

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

			String sql = "select URL from place where Content_Id = " + id + ";";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				url = rs.getString("URL");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException" + e.getMessage());
		}
		URL = url;
	}




	public String getURL() {
		return URL;
	}
}
