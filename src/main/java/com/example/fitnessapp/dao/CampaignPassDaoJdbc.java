package com.example.fitnessapp.dao;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import com.example.fitnessapp.model.CampaignPass;
import com.example.fitnessapp.database.DatabaseConfig;

public class CampaignPassDaoJdbc implements CampaignPassDAO {

    @Override
    public List<CampaignPass> findAll() {
        String sql = "SELECT id, campaign_id, pass_type_id FROM campaign_pass ORDER BY id";
        List<CampaignPass> campaignPasses = new ArrayList<>();

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                campaignPasses.add(mapRowToCampaignPass(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return campaignPasses;
    }

    @Override
    public Optional<CampaignPass> findById(int id) {
        String sql = "SELECT id, campaign_id, pass_type_id FROM campaign_pass WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToCampaignPass(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public void save(CampaignPass campaignPass) {
        String sql = "INSERT INTO campaign_pass (campaign_id, pass_type_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"})) {

            ps.setInt(1, campaignPass.getCampaignId());
            ps.setInt(2, campaignPass.getPassTypeId());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    campaignPass.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CampaignPass campaignPass) {
        String sql = "UPDATE campaign_pass SET campaign_id = ?, pass_type_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, campaignPass.getCampaignId());
            ps.setInt(2, campaignPass.getPassTypeId());
            ps.setInt(3, campaignPass.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM campaign_pass WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CampaignPass mapRowToCampaignPass(ResultSet rs) throws SQLException {
        return new CampaignPass(
                rs.getInt("id"),
                rs.getInt("campaign_id"),
                rs.getInt("pass_type_id")
        );
    }
}