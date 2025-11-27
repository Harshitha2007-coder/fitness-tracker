package com.fitnesstracker.dao;

import com.fitnesstracker.model.DailySteps;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Daily Steps operations.
 */
public class DailyStepsDAO {

    /**
     * Create or update daily steps entry.
     */
    public DailySteps createOrUpdate(DailySteps steps) throws SQLException {
        String sql = "INSERT INTO daily_steps (user_id, step_count, step_date, goal_steps) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE step_count = ?, goal_steps = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, steps.getUserId());
            stmt.setInt(2, steps.getStepCount());
            stmt.setDate(3, Date.valueOf(steps.getStepDate()));
            stmt.setInt(4, steps.getGoalSteps());
            stmt.setInt(5, steps.getStepCount());
            stmt.setInt(6, steps.getGoalSteps());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    steps.setId(generatedKeys.getInt(1));
                }
            }
            return steps;
        }
    }

    /**
     * Find steps for a specific date.
     */
    public Optional<DailySteps> findByUserIdAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM daily_steps WHERE user_id = ? AND step_date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDailySteps(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get steps history for a user within a date range.
     */
    public List<DailySteps> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT * FROM daily_steps WHERE user_id = ? AND step_date BETWEEN ? AND ? " +
                     "ORDER BY step_date DESC";
        List<DailySteps> stepsList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stepsList.add(mapResultSetToDailySteps(rs));
                }
            }
        }
        return stepsList;
    }

    /**
     * Get total steps for a user in a date range.
     */
    public int getTotalSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(step_count), 0) FROM daily_steps " +
                     "WHERE user_id = ? AND step_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get average daily steps for a user in a date range.
     */
    public double getAverageSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(AVG(step_count), 0) FROM daily_steps " +
                     "WHERE user_id = ? AND step_date BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get number of days goal was achieved.
     */
    public int getDaysGoalAchieved(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM daily_steps " +
                     "WHERE user_id = ? AND step_date BETWEEN ? AND ? AND step_count >= goal_steps";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Delete steps entry.
     */
    public void delete(int stepsId) throws SQLException {
        String sql = "DELETE FROM daily_steps WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, stepsId);
            stmt.executeUpdate();
        }
    }

    private DailySteps mapResultSetToDailySteps(ResultSet rs) throws SQLException {
        DailySteps steps = new DailySteps();
        steps.setId(rs.getInt("id"));
        steps.setUserId(rs.getInt("user_id"));
        steps.setStepCount(rs.getInt("step_count"));
        steps.setStepDate(rs.getDate("step_date").toLocalDate());
        steps.setGoalSteps(rs.getInt("goal_steps"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            steps.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return steps;
    }
}
