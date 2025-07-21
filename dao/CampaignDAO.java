package dao;

import java.sql.*;
import model.Campaign;

public class CampaignDAO {

    public static void insertCampaign(double deposit, double bonus) {
        String sql = "INSERT INTO campaigns (deposit_amount, bonus_amount) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, deposit);
            pstmt.setDouble(2, bonus);
            pstmt.executeUpdate();

            // Get the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int campaignId = generatedKeys.getInt(1);
                    Campaign campaign = new Campaign(campaignId, deposit, bonus);
                    MemoryStore.campaigns.put(campaignId, campaign);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert campaign", e);
        }
    }

    public static void deleteCampaign(int campaignId) {
        String sql = "DELETE FROM campaigns WHERE id = ?";
        try (PreparedStatement pstmt = Database.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, campaignId);
            pstmt.executeUpdate();

            // Remove from in-memory store
            MemoryStore.campaigns.remove(campaignId);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete campaign", e);
        }
    }

    public static ResultSet getAllCampaigns() throws SQLException {
        String sql = "SELECT id, deposit_amount, bonus_amount FROM campaigns";
        return Database.getConnection().createStatement().executeQuery(sql);
    }
}
