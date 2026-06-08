package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservationDAOJdbc implements ReservationDAO {

    @Override
    public List<Reservation> findByClientId(int clientId) {
        String sql = "SELECT * FROM reservation WHERE client_id = ? ORDER BY reservation_date DESC";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania rezerwacji klienta: " + clientId, e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByClassId(int classId) {
        String sql = "SELECT * FROM reservation WHERE class_id = ? ORDER BY reservation_date";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania rezerwacji dla zajęć: " + classId, e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByTrainerId(int trainerId) {
        String sql = "SELECT * FROM reservation WHERE trainer_id = ? ORDER BY reservation_date DESC";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trainerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania rezerwacji trenera: " + trainerId, e);
        }
        return list;
    }

    @Override
    public List<Reservation> findByStatus(String status) {
        String sql = "SELECT * FROM reservation WHERE status = ? ORDER BY reservation_date DESC";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania rezerwacji według statusu: " + status, e);
        }
        return list;
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT * FROM reservation ORDER BY reservation_date DESC";
        List<Reservation> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania wszystkich rezerwacji", e);
        }
        return list;
    }

    @Override
    public Optional<Reservation> findById(int id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania rezerwacji o ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void save(Reservation r) {
        String sql = "INSERT INTO reservation " +
                "(client_id, class_id, trainer_id, reservation_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID"})) {
            ps.setInt(1, r.getClientId());
            if (r.getClassId() > 0) ps.setInt(2, r.getClassId());
            else ps.setNull(2, Types.INTEGER);
            if (r.getTrainerId() > 0) ps.setInt(3, r.getTrainerId());
            else ps.setNull(3, Types.INTEGER);

            ps.setDate(4, Date.valueOf(r.getReservationDate()));
            ps.setString(5, r.getStatus());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd zapisu rezerwacji", e);
        }
    }

    @Override
    public void update(Reservation r) {
        String sql = "UPDATE reservation SET client_id = ?, class_id = ?, trainer_id = ?, " +
                "reservation_date = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getClientId());

            if (r.getClassId() > 0) ps.setInt(2, r.getClassId());
            else ps.setNull(2, Types.INTEGER);

            if (r.getTrainerId() > 0) ps.setInt(3, r.getTrainerId());
            else ps.setNull(3, Types.INTEGER);

            ps.setDate(4, Date.valueOf(r.getReservationDate()));
            ps.setString(5, r.getStatus());
            ps.setInt(6, r.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd aktualizacji rezerwacji: " + r.getId(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd usuwania rezerwacji: " + id, e);
        }
    }

    @Override
    public void cancel(int reservationId) {
        String sql = "UPDATE reservation SET status = 'CANCELED' WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Błąd anulowania rezerwacji: " + reservationId, e);
        }
    }

    @Override
    public boolean hasAvailableSpots(int classId) {
        String capacitySql = "SELECT capacity FROM group_class WHERE id = ?";
        int capacity = 0;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(capacitySql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    capacity = rs.getInt("capacity");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd pobierania pojemności zajęć: " + classId, e);
        }

        int confirmedCount = countConfirmedReservations(classId);

        return confirmedCount < capacity;
    }

    @Override
    public int countConfirmedReservations(int classId) {
        String sql = "SELECT COUNT(*) as count FROM reservation WHERE class_id = ? AND status = 'CONFIRMED'";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Błąd liczenia rezerwacji dla zajęć: " + classId, e);
        }
        return 0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("client_id"),
                rs.getInt("class_id"),
                rs.getInt("trainer_id"),
                rs.getDate("reservation_date").toLocalDate(),
                rs.getString("status")
        );
    }
}