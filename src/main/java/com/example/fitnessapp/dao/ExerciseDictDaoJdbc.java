package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.ExerciseDict;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseDictDaoJdbc implements ExerciseDictDAO {

    @Override
    public Optional<ExerciseDict> findById(int id) {
        String sql = "SELECT * FROM exercise_dict WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ExerciseDict(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("technique_description"),
                            rs.getString("muscle_group")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }
    @Override
    public List<ExerciseDict> findAll() {
        String sql = "SELECT * FROM exercise_dict ORDER BY muscle_group, name";
        List<ExerciseDict> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ExerciseDict(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("technique_description"),
                        rs.getString("muscle_group")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void save(ExerciseDict exercise) {
        String sql = "INSERT INTO exercise_dict (name, muscle_group, technique_description, required_equipment) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {

            ps.setString(1, exercise.getName());
            ps.setString(2, exercise.getMuscleGroup());
            ps.setString(3, exercise.getDescription());
            ps.setString(4, ""); // required_equipment - możesz dodać pole do modelu jeśli chcesz

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    exercise.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}