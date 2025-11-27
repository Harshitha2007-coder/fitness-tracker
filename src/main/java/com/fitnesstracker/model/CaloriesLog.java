package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CaloriesLog model for tracking daily calorie intake and burn.
 */
public class CaloriesLog {
    private int id;
    private int userId;
    private int caloriesConsumed;
    private int caloriesBurned;
    private LocalDate logDate;
    private LocalDateTime createdAt;

    public CaloriesLog() {}

    public CaloriesLog(int userId, int caloriesConsumed, int caloriesBurned, LocalDate logDate) {
        this.userId = userId;
        this.caloriesConsumed = caloriesConsumed;
        this.caloriesBurned = caloriesBurned;
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

    public int getCaloriesConsumed() {
        return caloriesConsumed;
    }

    public void setCaloriesConsumed(int caloriesConsumed) {
        this.caloriesConsumed = caloriesConsumed;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
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

    /**
     * Calculate net calories (consumed - burned).
     */
    public int getNetCalories() {
        return caloriesConsumed - caloriesBurned;
    }

    @Override
    public String toString() {
        return "CaloriesLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", caloriesConsumed=" + caloriesConsumed +
                ", caloriesBurned=" + caloriesBurned +
                ", logDate=" + logDate +
                '}';
    }
}
