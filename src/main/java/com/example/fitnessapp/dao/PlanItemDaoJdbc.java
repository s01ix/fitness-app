package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.PlanItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanItemDaoJdbc implements PlanItemDAO {
    @Override
    public List<PlanItem> findByPlanId(int planId) {
        String sql = "SELECT * FROM plan_item WHERE plan_id = ?";
        List<PlanItem> items = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new PlanItem(
                            rs.getInt("id"), rs.getInt("plan_id"), rs.getInt("exercise_id"),
                            rs.getInt("sets"), rs.getInt("reps"), "N/A"
                    ));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return items;
    }

    @Override
    public void save(PlanItem i) {
        String sql = "INSERT INTO plan_item (plan_id, exercise_id, sets, reps) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, i.getPlanId());
            ps.setInt(2, i.getExerciseId());
            ps.setInt(3, i.getSets());
            ps.setInt(4, i.getReps());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}