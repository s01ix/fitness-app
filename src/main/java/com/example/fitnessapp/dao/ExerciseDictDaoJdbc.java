package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.ExerciseDict;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDictDaoJdbc implements ExerciseDictDAO {
    @Override
    public List<ExerciseDict> findAll() {
        String sql = "SELECT * FROM exercise_dict ORDER BY muscle_group, name";
        List<ExerciseDict> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ExerciseDict(
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("technique_description"), rs.getString("muscle_group")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }
}