package com.fitnesstracker.dao;

import com.fitnesstracker.model.CalorieIntake;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Calorie Intake operations.
 */
public class CalorieIntakeDAO {

    /**
     * Create a new calorie intake entry.
     */
    public CalorieIntake create(CalorieIntake intake) throws SQLException {
        String sql = "INSERT INTO calorie_intake (user_id, meal_type, food_item, calories, " +
                     "protein_g, carbs_g, fat_g, intake_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, intake.getUserId());
            stmt.setString(2, intake.getMealType());
            stmt.setString(3, intake.getFoodItem());
            stmt.setInt(4, intake.getCalories());
            stmt.setDouble(5, intake.getProteinG());
            stmt.setDouble(6, intake.getCarbsG());
            stmt.setDouble(7, intake.getFatG());
            stmt.setDate(8, Date.valueOf(intake.getIntakeDate()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    intake.setId(generatedKeys.getInt(1));
                }
            }
            return intake;
        }
    }

    /**
     * Find calorie intake by ID.
     */
    public Optional<CalorieIntake> findById(int id) throws SQLException {
        String sql = "SELECT * FROM calorie_intake WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCalorieIntake(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all calorie intake entries for a user on a specific date.
     */
    public List<CalorieIntake> findByUserIdAndDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM calorie_intake WHERE user_id = ? AND intake_date = ? " +
                     "ORDER BY meal_type";
        List<CalorieIntake> intakes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    intakes.add(mapResultSetToCalorieIntake(rs));
                }
            }
        }
        return intakes;
    }

    /**
     * Get calorie intake entries for a date range.
     */
    public List<CalorieIntake> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT * FROM calorie_intake WHERE user_id = ? AND intake_date BETWEEN ? AND ? " +
                     "ORDER BY intake_date DESC, meal_type";
        List<CalorieIntake> intakes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    intakes.add(mapResultSetToCalorieIntake(rs));
                }
            }
        }
        return intakes;
    }

    /**
     * Get total calories for a specific date.
     */
    public int getTotalCaloriesForDate(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(calories), 0) FROM calorie_intake " +
                     "WHERE user_id = ? AND intake_date = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Get total calories for a date range.
     */
    public int getTotalCalories(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT COALESCE(SUM(calories), 0) FROM calorie_intake " +
                     "WHERE user_id = ? AND intake_date BETWEEN ? AND ?";
        
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
     * Get average daily calorie intake.
     */
    public double getAverageDailyCalories(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT COALESCE(AVG(daily_total), 0) FROM " +
                     "(SELECT SUM(calories) as daily_total FROM calorie_intake " +
                     "WHERE user_id = ? AND intake_date BETWEEN ? AND ? " +
                     "GROUP BY intake_date) as daily_totals";
        
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
     * Update a calorie intake entry.
     */
    public void update(CalorieIntake intake) throws SQLException {
        String sql = "UPDATE calorie_intake SET meal_type = ?, food_item = ?, calories = ?, " +
                     "protein_g = ?, carbs_g = ?, fat_g = ?, intake_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, intake.getMealType());
            stmt.setString(2, intake.getFoodItem());
            stmt.setInt(3, intake.getCalories());
            stmt.setDouble(4, intake.getProteinG());
            stmt.setDouble(5, intake.getCarbsG());
            stmt.setDouble(6, intake.getFatG());
            stmt.setDate(7, Date.valueOf(intake.getIntakeDate()));
            stmt.setInt(8, intake.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a calorie intake entry.
     */
    public void delete(int intakeId) throws SQLException {
        String sql = "DELETE FROM calorie_intake WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, intakeId);
            stmt.executeUpdate();
        }
    }

    private CalorieIntake mapResultSetToCalorieIntake(ResultSet rs) throws SQLException {
        CalorieIntake intake = new CalorieIntake();
        intake.setId(rs.getInt("id"));
        intake.setUserId(rs.getInt("user_id"));
        intake.setMealType(rs.getString("meal_type"));
        intake.setFoodItem(rs.getString("food_item"));
        intake.setCalories(rs.getInt("calories"));
        intake.setProteinG(rs.getDouble("protein_g"));
        intake.setCarbsG(rs.getDouble("carbs_g"));
        intake.setFatG(rs.getDouble("fat_g"));
        intake.setIntakeDate(rs.getDate("intake_date").toLocalDate());
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            intake.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return intake;
    }
}
