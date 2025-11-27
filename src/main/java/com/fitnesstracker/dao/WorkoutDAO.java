package com.fitnesstracker.dao;

import com.fitnesstracker.model.Workout;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Workout operations.
 */
public class WorkoutDAO {

    /**
     * Create a new workout entry.
     */
    public Workout create(Workout workout) throws SQLException {
        String sql = "INSERT INTO workouts (user_id, workout_type, duration_minutes, " +
                     "calories_burned, intensity, notes, workout_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, workout.getUserId());
            stmt.setString(2, workout.getWorkoutType());
            stmt.setInt(3, workout.getDurationMinutes());
            stmt.setInt(4, workout.getCaloriesBurned());
            stmt.setString(5, workout.getIntensity());
            stmt.setString(6, workout.getNotes());
            stmt.setDate(7, Date.valueOf(workout.getWorkoutDate()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    workout.setId(generatedKeys.getInt(1));
                }
            }
            return workout;
        }
    }

    /**
     * Find a workout by ID.
     */
    public Optional<Workout> findById(int id) throws SQLException {
        String sql = "SELECT * FROM workouts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToWorkout(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all workouts for a user.
     */
    public List<Workout> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM workouts WHERE user_id = ? ORDER BY workout_date DESC";
        List<Workout> workouts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapResultSetToWorkout(rs));
                }
            }
        }
        return workouts;
    }

    /**
     * Get workouts for a user within a date range.
     */
    public List<Workout> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT * FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ? " +
                     "ORDER BY workout_date DESC";
        List<Workout> workouts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    workouts.add(mapResultSetToWorkout(rs));
                }
            }
        }
        return workouts;
    }

    /**
     * Get total workout duration for a user in a date range.
     */
    public int getTotalDuration(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(duration_minutes), 0) FROM workouts " +
                     "WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        
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
     * Get total calories burned for a user in a date range.
     */
    public int getTotalCaloriesBurned(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT COALESCE(SUM(calories_burned), 0) FROM workouts " +
                     "WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        
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
     * Update a workout entry.
     */
    public void update(Workout workout) throws SQLException {
        String sql = "UPDATE workouts SET workout_type = ?, duration_minutes = ?, " +
                     "calories_burned = ?, intensity = ?, notes = ?, workout_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, workout.getWorkoutType());
            stmt.setInt(2, workout.getDurationMinutes());
            stmt.setInt(3, workout.getCaloriesBurned());
            stmt.setString(4, workout.getIntensity());
            stmt.setString(5, workout.getNotes());
            stmt.setDate(6, Date.valueOf(workout.getWorkoutDate()));
            stmt.setInt(7, workout.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a workout entry.
     */
    public void delete(int workoutId) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, workoutId);
            stmt.executeUpdate();
        }
    }

    /**
     * Get workout count for a user.
     */
    public int getWorkoutCount(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COUNT(*) FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        
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

    private Workout mapResultSetToWorkout(ResultSet rs) throws SQLException {
        Workout workout = new Workout();
        workout.setId(rs.getInt("id"));
        workout.setUserId(rs.getInt("user_id"));
        workout.setWorkoutType(rs.getString("workout_type"));
        workout.setDurationMinutes(rs.getInt("duration_minutes"));
        workout.setCaloriesBurned(rs.getInt("calories_burned"));
        workout.setIntensity(rs.getString("intensity"));
        workout.setNotes(rs.getString("notes"));
        workout.setWorkoutDate(rs.getDate("workout_date").toLocalDate());
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            workout.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return workout;
    }
}
