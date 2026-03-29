package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.TrainingPlan;
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
}