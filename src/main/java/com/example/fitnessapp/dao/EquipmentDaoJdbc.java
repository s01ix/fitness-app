package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Equipment;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class EquipmentDaoJdbc implements EquipmentDAO {

    @Override
    public List<Equipment> findAll() {
        String sql = "SELECT id, club_id, name, status, last_inspection_date FROM equipment";
        List<Equipment> inventory = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                inventory.add(mapRowToEquipment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return inventory;
    }

    @Override
    public List<Equipment> findByClubId(int clubId) {
        String sql = "SELECT * FROM equipment WHERE club_id = ?";
        List<Equipment> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clubId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRowToEquipment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @Override
    public Optional<Equipment> findById(int id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRowToEquipment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void save(Equipment e) {
        String sql = "INSERT INTO equipment (club_id, name, status, last_inspection_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {

            ps.setInt(1, e.getClubId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getStatus());
            ps.setDate(4, Date.valueOf(e.getLastInspectionDate()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Equipment e) {
        String sql = "UPDATE equipment SET club_id = ?, name = ?, status = ?, last_inspection_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, e.getClubId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getStatus());
            ps.setDate(4, Date.valueOf(e.getLastInspectionDate()));
            ps.setInt(5, e.getId());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Equipment mapRowToEquipment(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getInt("club_id"),
                rs.getString("name"),
                rs.getString("status"),
                rs.getDate("last_inspection_date").toLocalDate()
        );
    }
}
