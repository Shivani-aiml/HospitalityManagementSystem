package com.hms.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    // Database URL: jdbc:mysql://hostname:port/database_name
    private static final String URL = "jdbc:mysql://localhost:3306/hms_db";
    
    // Database Credentials
    private static final String USER = "root";
    
    // TODO: DELETE "YOUR_MYSQL_PASSWORD" AND TYPE YOUR ACTUAL PASSWORD INSIDE THE QUOTES
    private static final String PASSWORD = "password"; 

    public static Connection connect() throws SQLException {
        try {
            // Load the MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
    }
}