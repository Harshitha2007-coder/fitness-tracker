package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * CalorieIntake model for tracking food and calorie consumption.
 */
public class CalorieIntake {
    private int id;
    private int userId;
    private String mealType;
    private String foodItem;
    private int calories;
    private double proteinG;
    private double carbsG;
    private double fatG;
    private LocalDate intakeDate;
    private LocalDateTime createdAt;

    public CalorieIntake() {
    }

    public CalorieIntake(int userId, String mealType, String foodItem, 
                         int calories, LocalDate intakeDate) {
        this.userId = userId;
        this.mealType = mealType;
        this.foodItem = foodItem;
        this.calories = calories;
        this.intakeDate = intakeDate;
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

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getProteinG() {
        return proteinG;
    }

    public void setProteinG(double proteinG) {
        this.proteinG = proteinG;
    }

    public double getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(double carbsG) {
        this.carbsG = carbsG;
    }

    public double getFatG() {
        return fatG;
    }

    public void setFatG(double fatG) {
        this.fatG = fatG;
    }

    public LocalDate getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(LocalDate intakeDate) {
        this.intakeDate = intakeDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "CalorieIntake{" +
                "mealType='" + mealType + '\'' +
                ", foodItem='" + foodItem + '\'' +
                ", calories=" + calories +
                ", intakeDate=" + intakeDate +
                '}';
    }
}
