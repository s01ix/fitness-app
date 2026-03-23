package com.example.fitnessapp.dao;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.Connection;
import com.example.fitnessapp.model.PromoCampaign;
import com.example.fitnessapp.database.DatabaseConfig;

public class PromoCampaignDaoJdbc implements PromoCampaignDAO {

    @Override
    public List<PromoCampaign> findAll() {
        String sql = "SELECT id, name, target_group, budget, start_date, end_date FROM promo_campaign ORDER BY id";
        List<PromoCampaign> campaigns = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                campaigns.add(mapRowToPromoCampaign(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return campaigns;
    }

    @Override
    public Optional<PromoCampaign> findById(int id) {
        String sql = "SELECT id, name, target_group, budget, start_date, end_date FROM promo_campaign WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPromoCampaign(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(PromoCampaign promoCampaign) {
        String sql = "INSERT INTO promo_campaign (name, target_group, budget, start_date, end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"})) {

            ps.setString(1, promoCampaign.getName());
            ps.setString(2, promoCampaign.getTargetGroup());
            ps.setBigDecimal(3, promoCampaign.getBudget());
            ps.setDate(4, Date.valueOf(promoCampaign.getStartDate()));
            ps.setDate(5, Date.valueOf(promoCampaign.getEndDate()));

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    promoCampaign.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(PromoCampaign promoCampaign) {
        String sql = "UPDATE promo_campaign SET name = ?, target_group = ?, budget = ?, start_date = ?, end_date = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, promoCampaign.getName());
            ps.setString(2, promoCampaign.getTargetGroup());
            ps.setBigDecimal(3, promoCampaign.getBudget());
            ps.setDate(4, Date.valueOf(promoCampaign.getStartDate()));
            ps.setDate(5, Date.valueOf(promoCampaign.getEndDate()));
            ps.setInt(6, promoCampaign.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM promo_campaign WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PromoCampaign mapRowToPromoCampaign(ResultSet rs) throws SQLException {
        return new PromoCampaign(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("target_group"),
                rs.getBigDecimal("budget"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate()
        );
    }
}