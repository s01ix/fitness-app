package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDaoJdbc implements PaymentDAO {
    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payment ORDER BY payment_date DESC";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payments.add(new Payment(
                        rs.getInt("id"), rs.getInt("gym_pass_id"), rs.getBigDecimal("amount"),
                        rs.getTimestamp("payment_date").toLocalDateTime(), rs.getString("method")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return payments;
    }

    @Override
    public Optional<Payment> findById(int id) {
        String sql = "SELECT * FROM payment WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Payment(
                        rs.getInt("id"), rs.getInt("gym_pass_id"), rs.getBigDecimal("amount"),
                        rs.getTimestamp("payment_date").toLocalDateTime(), rs.getString("method")
                ));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void save(Payment p) {
        String sql = "INSERT INTO payment (user_id, gym_pass_id, amount, payment_date, method, status) VALUES (1, ?, ?, ?, ?, 'COMPLETED')";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setInt(1, p.getGymPassId());
            ps.setBigDecimal(2, p.getAmount());
            ps.setTimestamp(3, Timestamp.valueOf(p.getPaymentDate()));
            ps.setString(4, p.getMethod());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM payment WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}