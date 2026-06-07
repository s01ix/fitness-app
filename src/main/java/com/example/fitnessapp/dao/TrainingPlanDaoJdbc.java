package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.TrainingPlan;
import com.example.fitnessapp.dto.TrainingPlanDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrainingPlanDaoJdbc implements TrainingPlanDAO {
    @Override
    public List<TrainingPlan> findByUserId(int userId) {
        String sql = "SELECT * FROM training_plan WHERE client_id = ?";
        List<TrainingPlan> plans = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    plans.add(new TrainingPlan(
                            rs.getInt("id"), rs.getInt("client_id"), rs.getInt("trainer_id"),
                            rs.getString("name"), rs.getDate("created_at").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return plans;
    }

    @Override
    public void save(TrainingPlan p) {
        String sql = "INSERT INTO training_plan (client_id, trainer_id, name, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setInt(1, p.getUserId());
            ps.setInt(2, p.getTrainerId());
            ps.setString(3, p.getGoal());
            ps.setDate(4, Date.valueOf(p.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<TrainingPlanDTO> getClientTrainingPlan(int clientId) {
        String sql = "SELECT tp.name AS plan_name, ed.name AS exercise_name, pi.sets, pi.reps, pi.weight, pi.rest_seconds " +
                "FROM training_plan tp " +
                "JOIN plan_item pi ON tp.id = pi.plan_id " +
                "JOIN exercise_dict ed ON pi.exercise_id = ed.id " +
                "WHERE tp.client_id = ? " +
                "ORDER BY tp.id, pi.id";

        List<TrainingPlanDTO> detailsList = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, clientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detailsList.add(new TrainingPlanDTO(
                            rs.getString("plan_name"),
                            rs.getString("exercise_name"),
                            rs.getInt("sets"),
                            rs.getInt("reps"),
                            rs.getBigDecimal("weight"),
                            rs.getInt("rest_seconds")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return detailsList;
    }
}