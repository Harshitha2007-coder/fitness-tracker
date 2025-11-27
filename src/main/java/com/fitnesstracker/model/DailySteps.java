package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DailySteps model for tracking daily step counts.
 */
public class DailySteps {
    private int id;
    private int userId;
    private int stepCount;
    private LocalDate stepDate;
    private int goalSteps;
    private LocalDateTime createdAt;

    public DailySteps() {
        this.goalSteps = 10000; // Default goal
    }

    public DailySteps(int userId, int stepCount, LocalDate stepDate) {
        this.userId = userId;
        this.stepCount = stepCount;
        this.stepDate = stepDate;
        this.goalSteps = 10000;
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

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public LocalDate getStepDate() {
        return stepDate;
    }

    public void setStepDate(LocalDate stepDate) {
        this.stepDate = stepDate;
    }

    public int getGoalSteps() {
        return goalSteps;
    }

    public void setGoalSteps(int goalSteps) {
        this.goalSteps = goalSteps;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getGoalProgress() {
        if (goalSteps == 0) return 0;
        return (double) stepCount / goalSteps * 100;
    }

    public boolean isGoalAchieved() {
        return stepCount >= goalSteps;
    }

    @Override
    public String toString() {
        return "DailySteps{" +
                "stepCount=" + stepCount +
                ", stepDate=" + stepDate +
                ", goalSteps=" + goalSteps +
                ", progress=" + String.format("%.1f%%", getGoalProgress()) +
                '}';
    }
}
