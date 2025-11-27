package com.fitnesstracker.dao;

import com.fitnesstracker.model.CaloriesLog;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for CaloriesLog operations.
 */
public class CaloriesLogDAO {

    /**
     * Create or update a calories log entry for a specific date.
     */
    public CaloriesLog logCalories(CaloriesLog caloriesLog) throws SQLException {
        String sql = "INSERT INTO calories_log (user_id, calories_consumed, calories_burned, log_date) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE calories_consumed = ?, calories_burned = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, caloriesLog.getUserId());
            stmt.setInt(2, caloriesLog.getCaloriesConsumed());
            stmt.setInt(3, caloriesLog.getCaloriesBurned());
            stmt.setDate(4, Date.valueOf(caloriesLog.getLogDate()));
            stmt.setInt(5, caloriesLog.getCaloriesConsumed());
            stmt.setInt(6, caloriesLog.getCaloriesBurned());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    caloriesLog.setId(generatedKeys.getInt(1));
                }
            }
        }
        return caloriesLog;
    }

    /**
     * Find calories log by user ID and date.
     */
    public Optional<CaloriesLog> findByUserIdAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM calories_log WHERE user_id = ? AND log_date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCaloriesLog(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get calories logs for a user within a date range.
     */
    public List<CaloriesLog> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM calories_log WHERE user_id = ? AND log_date BETWEEN ? AND ? ORDER BY log_date DESC";
        List<CaloriesLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToCaloriesLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Get all calories logs for a user.
     */
    public List<CaloriesLog> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM calories_log WHERE user_id = ? ORDER BY log_date DESC";
        List<CaloriesLog> logs = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToCaloriesLog(rs));
                }
            }
        }
        return logs;
    }

    /**
     * Get total calories consumed for a user in a date range.
     */
    public int getTotalCaloriesConsumed(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(calories_consumed), 0) as total FROM calories_log WHERE user_id = ? AND log_date BETWEEN ? AND ?";
        
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
     * Get total calories burned for a user in a date range.
     */
    public int getTotalCaloriesBurned(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(calories_burned), 0) as total FROM calories_log WHERE user_id = ? AND log_date BETWEEN ? AND ?";
        
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
     * Delete calories log entry.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM calories_log WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to CaloriesLog object.
     */
    private CaloriesLog mapResultSetToCaloriesLog(ResultSet rs) throws SQLException {
        CaloriesLog log = new CaloriesLog();
        log.setId(rs.getInt("id"));
        log.setUserId(rs.getInt("user_id"));
        log.setCaloriesConsumed(rs.getInt("calories_consumed"));
        log.setCaloriesBurned(rs.getInt("calories_burned"));
        log.setLogDate(rs.getDate("log_date").toLocalDate());
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            log.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return log;
    }
}
