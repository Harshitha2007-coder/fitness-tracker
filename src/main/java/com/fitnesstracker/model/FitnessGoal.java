package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * FitnessGoal model for tracking user fitness objectives.
 */
public class FitnessGoal {
    private int id;
    private int userId;
    private String goalType;
    private double targetValue;
    private double currentValue;
    private LocalDate targetDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FitnessGoal() {
        this.status = "IN_PROGRESS";
    }

    public FitnessGoal(int userId, String goalType, double targetValue, LocalDate targetDate) {
        this.userId = userId;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
        this.currentValue = 0;
        this.status = "IN_PROGRESS";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getProgressPercentage() {
        if (targetValue == 0) return 0;
        return Math.min(100, (currentValue / targetValue) * 100);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status) || currentValue >= targetValue;
    }

    @Override
    public String toString() {
        return "FitnessGoal{" +
                "goalType='" + goalType + '\'' +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", progress=" + String.format("%.1f%%", getProgressPercentage()) +
                ", status='" + status + '\'' +
                '}';
    }
}
