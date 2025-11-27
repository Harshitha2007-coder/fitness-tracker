package com.fitnesstracker.dao;

import com.fitnesstracker.model.BMICategory;
import com.fitnesstracker.model.HealthMetrics;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Health Metrics operations.
 */
public class HealthMetricsDAO {

    /**
     * Create a new health metrics entry.
     */
    public HealthMetrics create(HealthMetrics metrics) throws SQLException {
        String sql = "INSERT INTO health_metrics (user_id, weight_kg, height_cm, bmi, bmi_category, " +
                     "blood_pressure_systolic, blood_pressure_diastolic, resting_heart_rate, measurement_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, metrics.getUserId());
            stmt.setDouble(2, metrics.getWeightKg());
            stmt.setDouble(3, metrics.getHeightCm());
            stmt.setDouble(4, metrics.getBmi());
            stmt.setString(5, metrics.getBmiCategory().name());
            
            if (metrics.getBloodPressureSystolic() != null) {
                stmt.setInt(6, metrics.getBloodPressureSystolic());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            if (metrics.getBloodPressureDiastolic() != null) {
                stmt.setInt(7, metrics.getBloodPressureDiastolic());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            if (metrics.getRestingHeartRate() != null) {
                stmt.setInt(8, metrics.getRestingHeartRate());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setDate(9, Date.valueOf(metrics.getMeasurementDate()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    metrics.setId(generatedKeys.getInt(1));
                }
            }
            return metrics;
        }
    }

    /**
     * Get the latest health metrics for a user.
     */
    public Optional<HealthMetrics> findLatestByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_metrics WHERE user_id = ? ORDER BY measurement_date DESC LIMIT 1";
        
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
     * Get health metrics history for a user.
     */
    public List<HealthMetrics> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM health_metrics WHERE user_id = ? ORDER BY measurement_date DESC";
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
     * Get health metrics for a date range.
     */
    public List<HealthMetrics> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT * FROM health_metrics WHERE user_id = ? AND measurement_date BETWEEN ? AND ? " +
                     "ORDER BY measurement_date DESC";
        List<HealthMetrics> metricsList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    metricsList.add(mapResultSetToHealthMetrics(rs));
                }
            }
        }
        return metricsList;
    }

    /**
     * Get weight change over a period.
     */
    public double getWeightChange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sqlStart = "SELECT weight_kg FROM health_metrics WHERE user_id = ? " +
                          "AND measurement_date >= ? ORDER BY measurement_date ASC LIMIT 1";
        String sqlEnd = "SELECT weight_kg FROM health_metrics WHERE user_id = ? " +
                        "AND measurement_date <= ? ORDER BY measurement_date DESC LIMIT 1";
        
        Double startWeight = null;
        Double endWeight = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlStart)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, Date.valueOf(startDate));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        startWeight = rs.getDouble(1);
                    }
                }
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlEnd)) {
                stmt.setInt(1, userId);
                stmt.setDate(2, Date.valueOf(endDate));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        endWeight = rs.getDouble(1);
                    }
                }
            }
        }
        
        if (startWeight != null && endWeight != null) {
            return endWeight - startWeight;
        }
        return 0;
    }

    /**
     * Delete a health metrics entry.
     */
    public void delete(int metricsId) throws SQLException {
        String sql = "DELETE FROM health_metrics WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, metricsId);
            stmt.executeUpdate();
        }
    }

    private HealthMetrics mapResultSetToHealthMetrics(ResultSet rs) throws SQLException {
        HealthMetrics metrics = new HealthMetrics();
        metrics.setId(rs.getInt("id"));
        metrics.setUserId(rs.getInt("user_id"));
        metrics.setWeightKg(rs.getDouble("weight_kg"));
        metrics.setHeightCm(rs.getDouble("height_cm"));
        metrics.setBmi(rs.getDouble("bmi"));
        metrics.setBmiCategory(BMICategory.valueOf(rs.getString("bmi_category")));
        
        int systolic = rs.getInt("blood_pressure_systolic");
        if (!rs.wasNull()) {
            metrics.setBloodPressureSystolic(systolic);
        }
        
        int diastolic = rs.getInt("blood_pressure_diastolic");
        if (!rs.wasNull()) {
            metrics.setBloodPressureDiastolic(diastolic);
        }
        
        int heartRate = rs.getInt("resting_heart_rate");
        if (!rs.wasNull()) {
            metrics.setRestingHeartRate(heartRate);
        }
        
        metrics.setMeasurementDate(rs.getDate("measurement_date").toLocalDate());
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            metrics.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return metrics;
    }
}
