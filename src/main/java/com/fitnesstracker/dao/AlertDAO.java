package com.fitnesstracker.dao;

import com.fitnesstracker.model.Alert;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Alert operations.
 */
public class AlertDAO {

    /**
     * Create a new alert.
     */
    public Alert create(Alert alert) throws SQLException {
        String sql = "INSERT INTO alerts (user_id, alert_type, message, is_read) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, alert.getUserId());
            stmt.setString(2, alert.getAlertType());
            stmt.setString(3, alert.getMessage());
            stmt.setBoolean(4, alert.isRead());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    alert.setId(generatedKeys.getInt(1));
                }
            }
        }
        return alert;
    }

    /**
     * Get all alerts for a user.
     */
    public List<Alert> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE user_id = ? ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapResultSetToAlert(rs));
                }
            }
        }
        return alerts;
    }

    /**
     * Get unread alerts for a user.
     */
    public List<Alert> findUnreadByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM alerts WHERE user_id = ? AND is_read = FALSE ORDER BY created_at DESC";
        List<Alert> alerts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alerts.add(mapResultSetToAlert(rs));
                }
            }
        }
        return alerts;
    }

    /**
     * Mark alert as read.
     */
    public void markAsRead(int alertId) throws SQLException {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE id = ?";
        
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
        String sql = "UPDATE alerts SET is_read = TRUE WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get unread alert count for a user.
     */
    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM alerts WHERE user_id = ? AND is_read = FALSE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    /**
     * Delete alert.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM alerts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Delete old read alerts (older than 30 days).
     */
    public void deleteOldReadAlerts(int userId) throws SQLException {
        String sql = "DELETE FROM alerts WHERE user_id = ? AND is_read = TRUE AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to Alert object.
     */
    private Alert mapResultSetToAlert(ResultSet rs) throws SQLException {
        Alert alert = new Alert();
        alert.setId(rs.getInt("id"));
        alert.setUserId(rs.getInt("user_id"));
        alert.setAlertType(rs.getString("alert_type"));
        alert.setMessage(rs.getString("message"));
        alert.setRead(rs.getBoolean("is_read"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            alert.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return alert;
    }
}
