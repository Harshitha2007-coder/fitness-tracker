package com.fitnesstracker.dao;

import com.fitnesstracker.model.HealthAlert;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Health Alert operations.
 */
public class HealthAlertDAO {

    /**
     * Create a new health alert.
     */
    public HealthAlert create(HealthAlert alert) throws SQLException {
        String sql = "INSERT INTO health_alerts (user_id, alert_type, message, severity) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, alert.getUserId());
            stmt.setString(2, alert.getAlertType());
            stmt.setString(3, alert.getMessage());
            stmt.setString(4, alert.getSeverity());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    alert.setId(generatedKeys.getInt(1));
                }
            }
            return alert;
        }
    }

    /**
     * Get all unread alerts for a user.
     */
    public List<HealthAlert> findUnreadByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_alerts WHERE user_id = ? AND is_read = FALSE " +
                     "ORDER BY created_at DESC";
        List<HealthAlert> alerts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapResultSetToHealthAlert(rs));
                }
            }
        }
        return alerts;
    }

    /**
     * Get all alerts for a user.
     */
    public List<HealthAlert> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_alerts WHERE user_id = ? ORDER BY created_at DESC";
        List<HealthAlert> alerts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapResultSetToHealthAlert(rs));
                }
            }
        }
        return alerts;
    }

    /**
     * Mark an alert as read.
     */
    public void markAsRead(int alertId) throws SQLException {
        String sql = "UPDATE health_alerts SET is_read = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alertId);
            stmt.executeUpdate();
        }
    }

    /**
     * Mark all alerts as read for a user.
     */
    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE health_alerts SET is_read = TRUE WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Delete an alert.
     */
    public void delete(int alertId) throws SQLException {
        String sql = "DELETE FROM health_alerts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, alertId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get unread alert count for a user.
     */
    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM health_alerts WHERE user_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private HealthAlert mapResultSetToHealthAlert(ResultSet rs) throws SQLException {
        HealthAlert alert = new HealthAlert();
        alert.setId(rs.getInt("id"));
        alert.setUserId(rs.getInt("user_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setSeverity(rs.getString("severity"));
        alert.setRead(rs.getBoolean("is_read"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            alert.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return alert;
    }
}
