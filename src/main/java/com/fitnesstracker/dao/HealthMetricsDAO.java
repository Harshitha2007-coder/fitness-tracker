package com.fitnesstracker.dao;

import com.fitnesstracker.model.HealthMetrics;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for HealthMetrics operations.
 */
public class HealthMetricsDAO {

    /**
     * Create a new health metrics entry.
     */
    public HealthMetrics create(HealthMetrics metrics) throws SQLException {
        String sql = "INSERT INTO health_metrics (user_id, bmi, health_classification, weight_kg, height_cm) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, metrics.getUserId());
            stmt.setDouble(2, metrics.getBmi());
            stmt.setString(3, metrics.getHealthClassification());
            stmt.setDouble(4, metrics.getWeightKg());
            stmt.setDouble(5, metrics.getHeightCm());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    metrics.setId(generatedKeys.getInt(1));
                }
            }
        }
        return metrics;
    }

    /**
     * Get all health metrics for a user.
     */
    public List<HealthMetrics> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_metrics WHERE user_id = ? ORDER BY recorded_at DESC";
        List<HealthMetrics> metricsList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    metricsList.add(mapResultSetToHealthMetrics(rs));
                }
            }
        }
        return metricsList;
    }

    /**
     * Get the latest health metrics for a user.
     */
    public Optional<HealthMetrics> findLatestByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_metrics WHERE user_id = ? ORDER BY recorded_at DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToHealthMetrics(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Delete health metrics entry.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM health_metrics WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to HealthMetrics object.
     */
    private HealthMetrics mapResultSetToHealthMetrics(ResultSet rs) throws SQLException {
        HealthMetrics metrics = new HealthMetrics();
        metrics.setId(rs.getInt("id"));
        metrics.setUserId(rs.getInt("user_id"));
        metrics.setBmi(rs.getDouble("bmi"));
        metrics.setHealthClassification(rs.getString("health_classification"));
        metrics.setWeightKg(rs.getDouble("weight_kg"));
        metrics.setHeightCm(rs.getDouble("height_cm"));
        
        Timestamp recordedAt = rs.getTimestamp("recorded_at");
        if (recordedAt != null) {
            metrics.setRecordedAt(recordedAt.toLocalDateTime());
        }
        
        return metrics;
    }
}
