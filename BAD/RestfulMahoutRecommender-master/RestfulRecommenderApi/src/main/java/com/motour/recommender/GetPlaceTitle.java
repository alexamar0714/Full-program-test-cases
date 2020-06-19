package com.motour.recommender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GetPlaceTitle {
	private String title;
	
	public GetPlaceTitle(int id){
		Connection conn;
		Statement stmt = null;
		String title = null;

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

			String sql = "select Title from place where Content_Id = " + id + ";";

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				title = rs.getString("Title");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException" + e.getMessage());
		}
		this.title = title;
	}





	public String getTitle() {
		return this.title;
	}
}
