package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * StepsLog model for tracking daily step counts.
 */
public class StepsLog {
    private int id;
    private int userId;
    private int steps;
    private LocalDate logDate;
    private LocalDateTime createdAt;

    public StepsLog() {}

    public StepsLog(int userId, int steps, LocalDate logDate) {
        this.userId = userId;
        this.steps = steps;
        this.logDate = logDate;
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

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        this.logDate = logDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StepsLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", steps=" + steps +
                ", logDate=" + logDate +
                '}';
    }
}
