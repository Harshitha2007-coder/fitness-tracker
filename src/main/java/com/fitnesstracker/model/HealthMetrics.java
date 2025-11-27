package com.fitnesstracker.model;

import java.time.LocalDateTime;

/**
 * HealthMetrics model for storing BMI and health classification data.
 */
public class HealthMetrics {
    private int id;
    private int userId;
    private double bmi;
    private String healthClassification;
    private double weightKg;
    private double heightCm;
    private LocalDateTime recordedAt;

    public HealthMetrics() {}

    public HealthMetrics(int userId, double bmi, String healthClassification, double weightKg, double heightCm) {
        this.userId = userId;
        this.bmi = bmi;
        this.healthClassification = healthClassification;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
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

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public String getHealthClassification() {
        return healthClassification;
    }

    public void setHealthClassification(String healthClassification) {
        this.healthClassification = healthClassification;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    @Override
    public String toString() {
        return "HealthMetrics{" +
                "id=" + id +
                ", userId=" + userId +
                ", bmi=" + String.format("%.2f", bmi) +
                ", healthClassification='" + healthClassification + '\'' +
                ", weightKg=" + weightKg +
                ", heightCm=" + heightCm +
                '}';
    }
}
