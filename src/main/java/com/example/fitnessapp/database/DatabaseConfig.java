package com.example.fitnessapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String user = "admin";
    private static final String password = "oracle";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
