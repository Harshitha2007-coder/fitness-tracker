package com.fitnesstracker.dao;

import com.fitnesstracker.model.Goal;
import com.fitnesstracker.model.GoalStatus;
import com.fitnesstracker.model.GoalType;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Goal operations.
 */
public class GoalDAO {

    /**
     * Create a new goal.
     */
    public Goal create(Goal goal) throws SQLException {
        String sql = "INSERT INTO goals (user_id, goal_type, target_value, current_value, start_date, end_date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getGoalType().name());
            stmt.setDouble(3, goal.getTargetValue());
            stmt.setDouble(4, goal.getCurrentValue());
            stmt.setDate(5, Date.valueOf(goal.getStartDate()));
            stmt.setDate(6, Date.valueOf(goal.getEndDate()));
            stmt.setString(7, goal.getStatus().name());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    goal.setId(generatedKeys.getInt(1));
                }
            }
        }
        return goal;
    }

    /**
     * Find goal by ID.
     */
    public Optional<Goal> findById(int id) throws SQLException {
        String sql = "SELECT * FROM goals WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToGoal(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all goals for a user.
     */
    public List<Goal> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? ORDER BY created_at DESC";
        List<Goal> goals = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        }
        return goals;
    }

    /**
     * Get active goals for a user.
     */
    public List<Goal> findActiveByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? AND status = 'IN_PROGRESS' ORDER BY end_date ASC";
        List<Goal> goals = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        }
        return goals;
    }

    /**
     * Get goals by type for a user.
     */
    public List<Goal> findByUserIdAndType(int userId, GoalType goalType) throws SQLException {
        String sql = "SELECT * FROM goals WHERE user_id = ? AND goal_type = ? ORDER BY created_at DESC";
        List<Goal> goals = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, goalType.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(mapResultSetToGoal(rs));
                }
            }
        }
        return goals;
    }

    /**
     * Update goal progress.
     */
    public void updateProgress(int goalId, double currentValue) throws SQLException {
        String sql = "UPDATE goals SET current_value = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, currentValue);
            stmt.setInt(2, goalId);
            
            stmt.executeUpdate();
        }
    }

    /**
     * Update goal status.
     */
    public void updateStatus(int goalId, GoalStatus status) throws SQLException {
        String sql = "UPDATE goals SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, goalId);
            
            stmt.executeUpdate();
        }
    }

    /**
     * Update goal.
     */
    public void update(Goal goal) throws SQLException {
        String sql = "UPDATE goals SET goal_type = ?, target_value = ?, current_value = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, goal.getGoalType().name());
            stmt.setDouble(2, goal.getTargetValue());
            stmt.setDouble(3, goal.getCurrentValue());
            stmt.setDate(4, Date.valueOf(goal.getStartDate()));
            stmt.setDate(5, Date.valueOf(goal.getEndDate()));
            stmt.setString(6, goal.getStatus().name());
            stmt.setInt(7, goal.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Delete goal.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to Goal object.
     */
    private Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal();
        goal.setId(rs.getInt("id"));
        goal.setUserId(rs.getInt("user_id"));
        goal.setGoalType(GoalType.valueOf(rs.getString("goal_type")));
        goal.setTargetValue(rs.getDouble("target_value"));
        goal.setCurrentValue(rs.getDouble("current_value"));
        goal.setStartDate(rs.getDate("start_date").toLocalDate());
        goal.setEndDate(rs.getDate("end_date").toLocalDate());
        goal.setStatus(GoalStatus.valueOf(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            goal.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return goal;
    }
}
