package com.fitnesstracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HealthMetrics model for tracking BMI and other health measurements.
 */
public class HealthMetrics {
    private int id;
    private int userId;
    private double weightKg;
    private double heightCm;
    private double bmi;
    private BMICategory bmiCategory;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer restingHeartRate;
    private LocalDate measurementDate;
    private LocalDateTime createdAt;

    public HealthMetrics() {
    }

    public HealthMetrics(int userId, double weightKg, double heightCm, LocalDate measurementDate) {
        this.userId = userId;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.measurementDate = measurementDate;
        calculateBMI();
    }

    public void calculateBMI() {
        if (heightCm > 0 && weightKg > 0) {
            double heightM = heightCm / 100.0;
            this.bmi = weightKg / (heightM * heightM);
            this.bmiCategory = BMICategory.fromBMI(this.bmi);
        }
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

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
        calculateBMI();
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
        calculateBMI();
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
        this.bmiCategory = BMICategory.fromBMI(bmi);
    }

    public BMICategory getBmiCategory() {
        return bmiCategory;
    }

    public void setBmiCategory(BMICategory bmiCategory) {
        this.bmiCategory = bmiCategory;
    }

    public Integer getBloodPressureSystolic() {
        return bloodPressureSystolic;
    }

    public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
        this.bloodPressureSystolic = bloodPressureSystolic;
    }

    public Integer getBloodPressureDiastolic() {
        return bloodPressureDiastolic;
    }

    public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
        this.bloodPressureDiastolic = bloodPressureDiastolic;
    }

    public Integer getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(Integer restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBloodPressureReading() {
        if (bloodPressureSystolic != null && bloodPressureDiastolic != null) {
            return bloodPressureSystolic + "/" + bloodPressureDiastolic + " mmHg";
        }
        return "Not recorded";
    }

    @Override
    public String toString() {
        return "HealthMetrics{" +
                "weightKg=" + weightKg +
                ", heightCm=" + heightCm +
                ", bmi=" + String.format("%.1f", bmi) +
                ", bmiCategory=" + bmiCategory +
                ", measurementDate=" + measurementDate +
                '}';
    }
}
