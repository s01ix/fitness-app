package com.example.fitnessapp.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final String DATABASEFILEPATH = "src/main/resources/db/schema.sql";
    public static void init(){
        String sql;
        try {
            sql = new String(Files.readAllBytes(Paths.get(DATABASEFILEPATH)));
        } catch (IOException e) {
            throw new RuntimeException("Nie można odczytać pliku: " + DATABASEFILEPATH, e);
        }
        String[] statements = sql.split(";");
        try(Connection connection = DatabaseConfig.getConnection();
            Statement statement =connection.createStatement()){
            for (String singleStatement : statements) {
                String trimmed = singleStatement.trim();
                if(trimmed.isEmpty()) { continue; }
                try {
                    statement.execute(trimmed);
                }catch(SQLException e){
                    if(e.getErrorCode() == 955) {
                        System.out.println("Tabela już istnieje, pomijam: " + trimmed);
                    } else {
                        throw new RuntimeException("Błąd podczas wykonywania polecenia SQL: " + trimmed, e);
                    }
                }
            }
            System.out.println("Poprawnie uruchomiono skrypt SQL");
            }
        catch (SQLException e){
            throw new RuntimeException("Błąd podczas uruchamiania skryptu SQL: " + DATABASEFILEPATH, e);
        }
    }
}
