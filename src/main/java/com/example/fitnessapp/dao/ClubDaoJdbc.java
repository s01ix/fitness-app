package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Club;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClubDaoJdbc implements ClubDAO {

    @Override
    public List<Club> findAll() {
        String sql = "SELECT id, name, address, opening_hours FROM club ORDER BY id";
        List<Club> clubs = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Club club = new Club(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("opening_hours")
                );
                clubs.add(club);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return clubs;
    }

    @Override
    public Optional<Club> findById(int id) {
        String sql = "SELECT id, name, address, opening_hours FROM club WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Club club = new Club(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("address"),
                            rs.getString("opening_hours")
                    );
                    return Optional.of(club);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Club club) {
        String sql = "INSERT INTO club (name, address, opening_hours) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"})) {

            ps.setString(1, club.getName());
            ps.setString(2, club.getAddress());
            ps.setString(3, club.getOpeningHours());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    club.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Club club) {
        String sql = "UPDATE club SET name = ?, address = ?, opening_hours = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, club.getName());
            ps.setString(2, club.getAddress());
            ps.setString(3, club.getOpeningHours());
            ps.setInt(4, club.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM club WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}