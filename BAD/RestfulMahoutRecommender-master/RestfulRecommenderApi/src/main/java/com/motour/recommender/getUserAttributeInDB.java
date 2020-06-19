package com.motour.recommender;
import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;

public class getUserAttributeInDB {
	
public ArrayList<String> returnPreferProperty (int User_Id){
		
		Connection conn;
		Statement stmt=null;
		ArrayList<String> PropertyList = new ArrayList<String>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			System.err.print("ClassNotFoundException");
		}
			
		try {
			String jdbcUrl = "jdbc:mysql://localhost:3306/tourofall2?autoReconnect=true&useSSL=false";
			String userId = "root";
			String userPass = "465651";

			conn = DriverManager.getConnection(jdbcUrl, userId, userPass);
			stmt = conn.createStatement();
			
			String sql = "select * from user_preferences where user_id = " + User_Id + ";";
			
			ResultSet rs= stmt.executeQuery(sql);
			while(rs.next()){
				PropertyList.add(rs.getString("item_category_code"));
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("SQLException"  + e.getMessage());
			}
		return PropertyList;
		}
}
