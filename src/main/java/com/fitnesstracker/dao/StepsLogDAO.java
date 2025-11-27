package com.fitnesstracker.dao;

import com.fitnesstracker.model.StepsLog;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for StepsLog operations.
 */
public class StepsLogDAO {

    /**
     * Create or update a steps log entry for a specific date.
     */
    public StepsLog logSteps(StepsLog stepsLog) throws SQLException {
        String sql = "INSERT INTO steps_log (user_id, steps, log_date) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE steps = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, stepsLog.getUserId());
            stmt.setInt(2, stepsLog.getSteps());
            stmt.setDate(3, Date.valueOf(stepsLog.getLogDate()));
            stmt.setInt(4, stepsLog.getSteps());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    stepsLog.setId(generatedKeys.getInt(1));
                }
            }
        }
        return stepsLog;
    }

    /**
     * Find steps log by user ID and date.
     */
    public Optional<StepsLog> findByUserIdAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM steps_log WHERE user_id = ? AND log_date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStepsLog(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get steps logs for a user within a date range.
     */
    public List<StepsLog> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM steps_log WHERE user_id = ? AND log_date BETWEEN ? AND ? ORDER BY log_date DESC";
        List<StepsLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToStepsLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Get all steps logs for a user.
     */
    public List<StepsLog> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM steps_log WHERE user_id = ? ORDER BY log_date DESC";
        List<StepsLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToStepsLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Get total steps for a user in a date range.
     */
    public int getTotalSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(steps), 0) as total FROM steps_log WHERE user_id = ? AND log_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    /**
     * Get average steps for a user in a date range.
     */
    public double getAverageSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(AVG(steps), 0) as average FROM steps_log WHERE user_id = ? AND log_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average");
                }
            }
        }
        return 0;
    }

    /**
     * Delete steps log entry.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM steps_log WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to StepsLog object.
     */
    private StepsLog mapResultSetToStepsLog(ResultSet rs) throws SQLException {
        StepsLog log = new StepsLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setSteps(rs.getInt("steps"));
        log.setLogDate(rs.getDate("log_date").toLocalDate());
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            log.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return log;
    }
}
