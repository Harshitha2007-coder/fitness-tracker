package com.fitnesstracker.dao;

import com.fitnesstracker.model.PlanType;
import com.fitnesstracker.model.TrainerPlan;
import com.fitnesstracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for TrainerPlan operations.
 */
public class TrainerPlanDAO {

    /**
     * Create a new trainer plan.
     */
    public TrainerPlan create(TrainerPlan plan) throws SQLException {
        String sql = "INSERT INTO trainer_plans (trainer_id, client_id, plan_type, title, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, plan.getTrainerId());
            stmt.setInt(2, plan.getClientId());
            stmt.setString(3, plan.getPlanType().name());
            stmt.setString(4, plan.getTitle());
            stmt.setString(5, plan.getDescription());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    plan.setId(generatedKeys.getInt(1));
                }
            }
        }
        return plan;
    }

    /**
     * Find plan by ID.
     */
    public Optional<TrainerPlan> findById(int id) throws SQLException {
        String sql = "SELECT * FROM trainer_plans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTrainerPlan(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get all plans created by a trainer.
     */
    public List<TrainerPlan> findByTrainerId(int trainerId) throws SQLException {
        String sql = "SELECT * FROM trainer_plans WHERE trainer_id = ? ORDER BY created_at DESC";
        List<TrainerPlan> plans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, trainerId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToTrainerPlan(rs));
                }
            }
        }
        return plans;
    }

    /**
     * Get all plans for a client.
     */
    public List<TrainerPlan> findByClientId(int clientId) throws SQLException {
        String sql = "SELECT * FROM trainer_plans WHERE client_id = ? ORDER BY created_at DESC";
        List<TrainerPlan> plans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToTrainerPlan(rs));
                }
            }
        }
        return plans;
    }

    /**
     * Get plans for a client by type.
     */
    public List<TrainerPlan> findByClientIdAndType(int clientId, PlanType planType) throws SQLException {
        String sql = "SELECT * FROM trainer_plans WHERE client_id = ? AND plan_type = ? ORDER BY created_at DESC";
        List<TrainerPlan> plans = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, clientId);
            stmt.setString(2, planType.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    plans.add(mapResultSetToTrainerPlan(rs));
                }
            }
        }
        return plans;
    }

    /**
     * Update plan.
     */
    public void update(TrainerPlan plan) throws SQLException {
        String sql = "UPDATE trainer_plans SET plan_type = ?, title = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, plan.getPlanType().name());
            stmt.setString(2, plan.getTitle());
            stmt.setString(3, plan.getDescription());
            stmt.setInt(4, plan.getId());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Delete plan.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM trainer_plans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Map ResultSet to TrainerPlan object.
     */
    private TrainerPlan mapResultSetToTrainerPlan(ResultSet rs) throws SQLException {
        TrainerPlan plan = new TrainerPlan();
        plan.setId(rs.getInt("id"));
        plan.setTrainerId(rs.getInt("trainer_id"));
        plan.setClientId(rs.getInt("client_id"));
        plan.setPlanType(PlanType.valueOf(rs.getString("plan_type")));
        plan.setTitle(rs.getString("title"));
        plan.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            plan.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return plan;
    }
}
