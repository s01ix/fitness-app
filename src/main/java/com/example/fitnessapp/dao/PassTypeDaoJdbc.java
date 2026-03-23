package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.PassType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PassTypeDaoJdbc implements PassTypeDAO {

    @Override
    public List<PassType> findAll() {
        String sql = "SELECT id, name, base_price FROM pass_type ORDER BY id";
        List<PassType> passTypes = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                passTypes.add(mapRowToPassType(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return passTypes;
    }

    @Override
    public Optional<PassType> findById(int id) {
        String sql = "SELECT id, name, base_price FROM pass_type WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPassType(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(PassType passType) {
        String sql = "INSERT INTO pass_type (name, base_price) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"})) {

            ps.setString(1, passType.getName());
            ps.setBigDecimal(2, passType.getBasePrice());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    passType.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(PassType passType) {
        String sql = "UPDATE pass_type SET name = ?, base_price = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, passType.getName());
            ps.setBigDecimal(2, passType.getBasePrice());
            ps.setInt(3, passType.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM pass_type WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PassType mapRowToPassType(ResultSet rs) throws SQLException {
        return new PassType(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getBigDecimal("base_price")
        );
    }
}