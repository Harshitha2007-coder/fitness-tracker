package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Goal model for tracking fitness goals.
 */
public class Goal {
    private int id;
    private int userId;
    private GoalType goalType;
    private double targetValue;
    private double currentValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private GoalStatus status;
    private LocalDateTime createdAt;

    public Goal() {}

    public Goal(int userId, GoalType goalType, double targetValue, LocalDate startDate, LocalDate endDate) {
        this.userId = userId;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentValue = 0;
        this.status = GoalStatus.IN_PROGRESS;
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

    public GoalType getGoalType() {
        return goalType;
    }

    public void setGoalType(GoalType goalType) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public GoalStatus getStatus() {
        return status;
    }

    public void setStatus(GoalStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Calculate progress percentage towards the goal.
     */
    public double getProgressPercentage() {
        if (targetValue == 0) return 0;
        return Math.min(100, (currentValue / targetValue) * 100);
    }

    @Override
    public String toString() {
        return "Goal{" +
                "id=" + id +
                ", userId=" + userId +
                ", goalType=" + goalType +
                ", targetValue=" + targetValue +
                ", currentValue=" + currentValue +
                ", progress=" + String.format("%.1f", getProgressPercentage()) + "%" +
                ", status=" + status +
                '}';
    }
}
