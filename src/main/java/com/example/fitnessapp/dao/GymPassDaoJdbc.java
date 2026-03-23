package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.GymPass;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GymPassDaoJdbc implements GymPassDAO {

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