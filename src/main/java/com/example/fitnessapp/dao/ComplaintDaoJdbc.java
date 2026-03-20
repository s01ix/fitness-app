package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Complaint;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComplaintDaoJdbc implements ComplaintDAO {

    @Override
    public List<Complaint> findAll() {
        String sql = "SELECT * FROM complaint ORDER BY id DESC";
        List<Complaint> complaints = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) complaints.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return complaints;
    }

    @Override
    public void save(Complaint c) {
        String sql = "INSERT INTO complaint (author_id, description, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setInt(1, c.getAuthorId());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getStatus() != null ? c.getStatus() : "OPEN");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Complaint c) {
        String sql = "UPDATE complaint SET status = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getStatus());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Complaint> findByAuthorId(int authorId) {
        String sql = "SELECT * FROM complaint WHERE author_id = ?";
        List<Complaint> results = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return results;
    }

    @Override
    public Optional<Complaint> findById(int id) {
        String sql = "SELECT * FROM complaint WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM complaint WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        return new Complaint(
                rs.getInt("id"),
                rs.getInt("author_id"),
                rs.getString("description"),
                rs.getString("status")
        );
    }
}