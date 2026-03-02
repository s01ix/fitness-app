package com.example.fitnessapp;

import java.sql.*;

public class HelloApplication{
    public static void main(String [] args) {
        String url = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
        String user = "admin";
        String password = "oracle";
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Połączenie z bazą danych udane!");
            try (Statement stmt = conn.createStatement()) {
                try {
                    stmt.execute("DROP TABLE test");
                    System.out.println("Usunięto istniejącą tabelę test.");
                } catch (SQLException e) {}
                stmt.execute("CREATE TABLE test (" +
                        "id NUMBER PRIMARY KEY, " +
                        "nazwa VARCHAR2(100)" +
                        ")");
                System.out.println("Utworzono tabelę");
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO test (id, nazwa) VALUES (?, ?)")) {
                ps.setInt(1, 1);
                ps.setString(2, "karta");
                ps.executeUpdate();
                ps.setInt(1, 2);
                ps.setString(2, "długopis");
                ps.executeUpdate();
                System.out.println("Wstawiono dane ");
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, nazwa FROM test")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nazwa = rs.getString("nazwa");
                    System.out.println("id=" + id + ", nazwa=" + nazwa);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}