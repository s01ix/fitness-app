package com.example.fitnessapp.dao;

import com.example.fitnessapp.database.DatabaseConfig;
import com.example.fitnessapp.model.Discount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiscountDaoJdbc implements DiscountDAO {

    @Override
    public List<Discount> findAll() {
        String sql = "SELECT id, campaign_id, discount_type, discount_value FROM discount ORDER BY id";
        List<Discount> discounts = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                discounts.add(mapRowToDiscount(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return discounts;
    }

    @Override
    public Optional<Discount> findById(int id) {
        String sql = "SELECT id, campaign_id, discount_type, discount_value FROM discount WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDiscount(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(Discount discount) {
        String sql = "INSERT INTO discount (campaign_id, discount_type, discount_value) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"})) {

            ps.setInt(1, discount.getCampaignId());
            ps.setString(2, discount.getDiscountType());
            ps.setBigDecimal(3, discount.getDiscountValue());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    discount.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Discount discount) {
        String sql = "UPDATE discount SET campaign_id = ?, discount_type = ?, discount_value = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, discount.getCampaignId());
            ps.setString(2, discount.getDiscountType());
            ps.setBigDecimal(3, discount.getDiscountValue());
            ps.setInt(4, discount.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM discount WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Discount mapRowToDiscount(ResultSet rs) throws SQLException {
        return new Discount(
                rs.getInt("id"),
                rs.getInt("campaign_id"),
                rs.getString("discount_type"),
                rs.getBigDecimal("discount_value")
        );
    }
}