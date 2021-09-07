package com.xoriant.mqTest.JDBC_integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.*;

public class JDBC_Integration {
		//variables for database 
		public static Connection con;
		
		public static void setupConnections() {
			try{
				DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
				con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mq","root","1234");
			}catch(Exception e) {
				e.getMessage();
			}
			
		}
		
		public static void addDataToTable(int index,String data){
			try {
				setupConnections();
				Statement stmt = con.createStatement();
				String sqlStatement = "insert into MqMessages values("+index+",\'"+data+"\')";
				stmt.execute(sqlStatement);
				System.out.print(data +" has been pushed to the database");
				}catch (Exception e) {
					e.printStackTrace();
				}
		}
		
		public static String getDataFromDatabase(int index) {
			String value = "Unable to get value for "+index;
			try {
				setupConnections();
				Statement stmt = con.createStatement();

				ResultSet rs = stmt.executeQuery("select messageValue from MqMessages where srNo ="+index);
				rs.next();
				value = rs.getString(1);

				}catch (Exception e) {
					e.printStackTrace();
				}
			return value;
			
		}
		
		public static void main(String args[])
		{
//			try {
//			DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
//			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mq","root","1234");
//			Statement stmt = con.createStatement();
//		
//			//insert into MqMessages values(1,"hi");
//			String data ="hi";
//			String sqlStatement = "insert into MqMessages values("+"'\'"+data+"'\'"+")";
//			stmt.execute("insert into MqMessages values("+"\""+data+"\""+")");
//			
//			ResultSet rs = stmt.executeQuery("select messageValue from MqMessages where srNo ="+1);
//			rs.next();
//			System.out.println(rs.getString(1));
//
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
		}

}
