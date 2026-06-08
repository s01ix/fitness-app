package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.dto.GroupClassDTO;
import com.example.fitnessapp.model.GroupClass;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GroupClassDaoJdbc implements GroupClassDAO {

    public List<GroupClassDTO> getAvailableClassesWithDetails(){
        String sql = "SELECT gc.id, gc.name, gc.schedule_time, gc.capacity, u.first_name, u.last_name " +
                "FROM group_class gc " +
                "JOIN gym_user u ON gc.trainer_id = u.id " +
                "WHERE gc.status = 'SCHEDULED' AND gc.schedule_time > CURRENT_TIMESTAMP " +
                "ORDER BY gc.schedule_time ASC";

        List<GroupClassDTO> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String trainerName = rs.getString("first_name") + " " + rs.getString("last_name");
                list.add(new GroupClassDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getTimestamp("schedule_time").toLocalDateTime(),
                        trainerName,
                        rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<GroupClass> findAll() {
        String sql = "SELECT id, club_id, trainer_id, name, schedule_time, capacity, status FROM group_class ORDER BY schedule_time";
        List<GroupClass> classes = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                classes.add(mapRowToGroupClass(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return classes;
    }

    @Override
    public Optional<GroupClass> findById(int id) {
        String sql = "SELECT id, club_id, trainer_id, name, schedule_time, capacity, status FROM group_class WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToGroupClass(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public void save(GroupClass gc) {
        String sql = "INSERT INTO group_class (club_id, trainer_id, name, schedule_time, capacity, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {

            ps.setInt(1, gc.getClubId());
            ps.setInt(2, gc.getTrainerId());
            ps.setString(3, gc.getName());
            ps.setTimestamp(4, Timestamp.valueOf(gc.getScheduleTime()));
            ps.setInt(5, gc.getCapacity());
            ps.setString(6, gc.getStatus());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    gc.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(GroupClass gc) {
        String sql = "UPDATE group_class SET club_id = ?, trainer_id = ?, name = ?, schedule_time = ?, capacity = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gc.getClubId());
            ps.setInt(2, gc.getTrainerId());
            ps.setString(3, gc.getName());
            ps.setTimestamp(4, Timestamp.valueOf(gc.getScheduleTime()));
            ps.setInt(5, gc.getCapacity());
            ps.setString(6, gc.getStatus());
            ps.setInt(7, gc.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM group_class WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private GroupClass mapRowToGroupClass(ResultSet rs) throws SQLException {
        return new GroupClass(
                rs.getInt("id"),
                rs.getInt("club_id"),
                rs.getInt("trainer_id"),
                rs.getString("name"),
                rs.getTimestamp("schedule_time").toLocalDateTime(),
                rs.getInt("capacity"),
                rs.getString("status")
        );
    }
}