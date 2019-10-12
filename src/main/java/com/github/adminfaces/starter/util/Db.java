package com.github.adminfaces.starter.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

public class Db implements Serializable{
//	private static String server = "localhost";
//	private static String port = "3306";
//	private static String database = "temis";
//	private static String username = "root";
//	private static String password = "RMEK6078";
	
	private static String server = "localhost";
	private static String port = "3306";
	private static String database = "tanaemis";
	private static String username = "afiasoft";
	private static String password = "Acoustica1..";
	public static Connection conn;
	public static PreparedStatement ps;
	public static ResultSet rs;
	private static Logger logger = Logger.getLogger(Db.class);
	
	public Db() {
		// TODO Auto-generated constructor stub
	}

	public static Connection connect() {
        try {
        	Class.forName("com.mysql.jdbc.Driver");    	
            conn = DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + database, username, password);  
        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        	ex.printStackTrace();
        	logger.error(ex.getMessage());        
       }
        return conn;
	}
	
    public static void disconnect() {
        try {
           conn.close();
           ps.close();
           rs.close();
        } catch (Exception ex) {
        	ex.printStackTrace();
        	logger.error(ex.getMessage());
        }
    }
    
    public static Timestamp getCurrentTimeStamp(){
    	java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
    	return timestamp;
    }
}
