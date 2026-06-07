package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.GymPass;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GymPassDaoJdbc implements GymPassDAO {

    // metoda do transakcyjnego zakupu karnetu
    public boolean purchasePass(int userId, int passTypeId, BigDecimal price, String paymentMethod) {
        String insertPassSql = "INSERT INTO gym_pass (user_id, pass_type_id, price, purchase_date, expiration_date, status) VALUES (?, ?, ?, ?, ?, 'ACTIVE')";
        String insertPaymentSql = "INSERT INTO payment (user_id, gym_pass_id, amount, payment_date, method, status) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'COMPLETED')";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();

            conn.setAutoCommit(false);

            int newPassId;

            try (PreparedStatement psPass = conn.prepareStatement(insertPassSql, new String[]{"ID"})) {
                psPass.setInt(1, userId);
                psPass.setInt(2, passTypeId);
                psPass.setBigDecimal(3, price);

                java.time.LocalDate purchaseDate = java.time.LocalDate.now();
                java.time.LocalDate expirationDate = purchaseDate.plusDays(30);

                psPass.setDate(4, Date.valueOf(purchaseDate));
                psPass.setDate(5, Date.valueOf(expirationDate));

                psPass.executeUpdate();

                try (ResultSet rsKeys = psPass.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        newPassId = rsKeys.getInt(1);
                    } else {
                        throw new SQLException("Nie udało się pobrać ID nowego karnetu.");
                    }
                }
            }

            try (PreparedStatement psPayment = conn.prepareStatement(insertPaymentSql)) {
                psPayment.setInt(1, userId);
                psPayment.setInt(2, newPassId);
                psPayment.setBigDecimal(3, price);
                psPayment.setString(4, paymentMethod);

                psPayment.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<GymPass> findActiveByUserId(int userId) {
        String sql = "SELECT id, user_id, pass_type_id, price, purchase_date, expiration_date, status " +
                "FROM gym_pass WHERE user_id = ? AND status = 'ACTIVE' ORDER BY expiration_date ASC";
        List<GymPass> activePasses = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activePasses.add(mapRowToGymPass(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return activePasses;
    }

    @Override
    public List<GymPass> findAll() {
        String sql = "SELECT id, user_id, pass_type_id, price, purchase_date, expiration_date, status FROM gym_pass ORDER BY id";
        List<GymPass> gymPasses = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                gymPasses.add(mapRowToGymPass(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return gymPasses;
    }

    @Override
    public Optional<GymPass> findById(int id) {
        String sql = "SELECT id, user_id, pass_type_id, price, purchase_date, expiration_date, status FROM gym_pass WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGymPass(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(GymPass gymPass) {
        String sql = "INSERT INTO gym_pass (user_id, pass_type_id, price, purchase_date, expiration_date, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"})) {

            ps.setInt(1, gymPass.getUserId());
            ps.setInt(2, gymPass.getPassTypeId());
            ps.setBigDecimal(3, gymPass.getPrice());
            ps.setDate(4, Date.valueOf(gymPass.getPurchaseDate()));
            ps.setDate(5, Date.valueOf(gymPass.getExpirationDate()));
            ps.setString(6, gymPass.getStatus());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    gymPass.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(GymPass gymPass) {
        String sql = "UPDATE gym_pass SET user_id = ?, pass_type_id = ?, price = ?, purchase_date = ?, expiration_date = ?, status = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, gymPass.getUserId());
            ps.setInt(2, gymPass.getPassTypeId());
            ps.setBigDecimal(3, gymPass.getPrice());
            ps.setDate(4, Date.valueOf(gymPass.getPurchaseDate()));
            ps.setDate(5, Date.valueOf(gymPass.getExpirationDate()));
            ps.setString(6, gymPass.getStatus());
            ps.setInt(7, gymPass.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM gym_pass WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private GymPass mapRowToGymPass(ResultSet rs) throws SQLException {
        return new GymPass(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("pass_type_id"),
                rs.getBigDecimal("price"),
                rs.getDate("purchase_date").toLocalDate(),
                rs.getDate("expiration_date").toLocalDate(),
                rs.getString("status")
        );
    }
}