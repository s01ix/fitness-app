package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoJdbc implements MessageDAO {

    @Override
    public List<Message> findConversation(int u1, int u2) {
        String sql = "SELECT * FROM message WHERE (sender_id = ? AND receiver_id = ?) " +
                "OR (sender_id = ? AND receiver_id = ?) ORDER BY sent_at ASC";
        List<Message> history = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, u1); ps.setInt(2, u2);
            ps.setInt(3, u2); ps.setInt(4, u1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) history.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return history;
    }

    @Override
    public List<Message> findReceivedMessages(int userId) {
        String sql = "SELECT * FROM message WHERE receiver_id = ? ORDER BY sent_at DESC";
        List<Message> received = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) received.add(mapRow(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return received;
    }

    @Override
    public void save(Message m) {
        String sql = "INSERT INTO message (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setInt(1, m.getSenderId());
            ps.setInt(2, m.getReceiverId());
            ps.setString(3, m.getContent());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getInt(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM message WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Message mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("sent_at");
        return new Message(
                rs.getInt("id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getString("content"),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}