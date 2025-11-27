package com.fitnesstracker.service;

import com.fitnesstracker.dao.HealthAlertDAO;
import com.fitnesstracker.dao.HealthMetricsDAO;
import com.fitnesstracker.model.BMICategory;
import com.fitnesstracker.model.HealthAlert;
import com.fitnesstracker.model.HealthMetrics;
import com.fitnesstracker.model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Service for BMI calculations, health classification, and alerts.
 */
public class BMIService {
    
    private final HealthMetricsDAO healthMetricsDAO;
    private final HealthAlertDAO healthAlertDAO;

    public BMIService() {
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.healthAlertDAO = new HealthAlertDAO();
    }

    /**
     * Calculate BMI from weight and height.
     */
    public double calculateBMI(double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            throw new IllegalArgumentException("Weight and height must be positive values.");
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Get BMI category for a given BMI value.
     */
    public BMICategory classifyBMI(double bmi) {
        return BMICategory.fromBMI(bmi);
    }

    /**
     * Record health metrics and generate alerts if needed.
     */
    public HealthMetrics recordHealthMetrics(int userId, double weightKg, double heightCm) 
            throws SQLException {
        return recordHealthMetrics(userId, weightKg, heightCm, null, null, null);
    }

    /**
     * Record complete health metrics with optional blood pressure and heart rate.
     */
    public HealthMetrics recordHealthMetrics(int userId, double weightKg, double heightCm,
                                              Integer bpSystolic, Integer bpDiastolic, 
                                              Integer heartRate) throws SQLException {
        
        HealthMetrics metrics = new HealthMetrics(userId, weightKg, heightCm, LocalDate.now());
        metrics.setBloodPressureSystolic(bpSystolic);
        metrics.setBloodPressureDiastolic(bpDiastolic);
        metrics.setRestingHeartRate(heartRate);
        
        // Save metrics
        healthMetricsDAO.create(metrics);
        
        // Generate BMI alert if needed
        if (metrics.getBmiCategory().needsAlert()) {
            createBMIAlert(userId, metrics);
        }
        
        return metrics;
    }

    /**
     * Get latest BMI for a user.
     */
    public Optional<Double> getLatestBMI(int userId) throws SQLException {
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        return metrics.map(HealthMetrics::getBmi);
    }

    /**
     * Get latest BMI category for a user.
     */
    public Optional<BMICategory> getLatestBMICategory(int userId) throws SQLException {
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        return metrics.map(HealthMetrics::getBmiCategory);
    }

    /**
     * Get health assessment based on BMI.
     */
    public String getHealthAssessment(int userId) throws SQLException {
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(userId);
        
        if (metricsOpt.isEmpty()) {
            return "No health metrics recorded yet. Please record your weight and height.";
        }
        
        HealthMetrics metrics = metricsOpt.get();
        BMICategory category = metrics.getBmiCategory();
        
        StringBuilder assessment = new StringBuilder();
        assessment.append("Current BMI: ").append(String.format("%.1f", metrics.getBmi()));
        assessment.append("\nCategory: ").append(category.getDisplayName());
        assessment.append(" (").append(category.getRange()).append(")");
        assessment.append("\n\nRecommendation: ").append(category.getHealthAdvice());
        
        return assessment.toString();
    }

    /**
     * Get ideal weight range based on height.
     */
    public String getIdealWeightRange(double heightCm) {
        double heightM = heightCm / 100.0;
        double minWeight = 18.5 * heightM * heightM;
        double maxWeight = 24.9 * heightM * heightM;
        
        return String.format("For your height (%.0f cm), the ideal weight range is %.1f - %.1f kg",
                           heightCm, minWeight, maxWeight);
    }

    /**
     * Calculate target weight for normal BMI.
     */
    public double getTargetWeight(double heightCm, double targetBMI) {
        double heightM = heightCm / 100.0;
        return targetBMI * heightM * heightM;
    }

    /**
     * Get personalized goals based on BMI category.
     */
    public String getPersonalizedGoals(User user) throws SQLException {
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(user.getId());
        
        if (metricsOpt.isEmpty()) {
            return "Please record your health metrics first to get personalized goals.";
        }
        
        HealthMetrics metrics = metricsOpt.get();
        BMICategory category = metrics.getBmiCategory();
        StringBuilder goals = new StringBuilder();
        
        goals.append("Personalized Goals for ").append(user.getFirstName()).append(":\n\n");
        
        switch (category) {
            case UNDERWEIGHT:
                goals.append("1. Increase daily calorie intake by 300-500 calories\n");
                goals.append("2. Focus on strength training to build muscle mass\n");
                goals.append("3. Eat protein-rich foods with every meal\n");
                goals.append("4. Target weight gain: 0.5 kg per week\n");
                double targetWeightUW = getTargetWeight(metrics.getHeightCm(), 20.0);
                goals.append("5. Target weight: ").append(String.format("%.1f kg", targetWeightUW));
                break;
                
            case NORMAL:
                goals.append("1. Maintain current healthy weight\n");
                goals.append("2. Exercise 150+ minutes per week\n");
                goals.append("3. Walk 10,000 steps daily\n");
                goals.append("4. Maintain balanced diet of 2000-2500 calories\n");
                goals.append("5. Get regular health check-ups");
                break;
                
            case OVERWEIGHT:
                goals.append("1. Reduce daily calorie intake by 300-500 calories\n");
                goals.append("2. Exercise 200+ minutes per week\n");
                goals.append("3. Walk 12,000+ steps daily\n");
                goals.append("4. Target weight loss: 0.5 kg per week\n");
                double targetWeightOW = getTargetWeight(metrics.getHeightCm(), 23.0);
                goals.append("5. Target weight: ").append(String.format("%.1f kg", targetWeightOW));
                break;
                
            case OBESE:
                goals.append("1. Consult a healthcare provider for a comprehensive plan\n");
                goals.append("2. Reduce daily calorie intake by 500-750 calories\n");
                goals.append("3. Start with low-impact exercises (walking, swimming)\n");
                goals.append("4. Target sustainable weight loss: 0.5-1 kg per week\n");
                double targetWeightOB = getTargetWeight(metrics.getHeightCm(), 24.0);
                goals.append("5. Initial target weight: ").append(String.format("%.1f kg", targetWeightOB));
                break;
        }
        
        return goals.toString();
    }

    private void createBMIAlert(int userId, HealthMetrics metrics) throws SQLException {
        String alertType = "BMI_WARNING";
        String severity;
        String message;
        
        switch (metrics.getBmiCategory()) {
            case UNDERWEIGHT:
                severity = "WARNING";
                message = "Your BMI (" + String.format("%.1f", metrics.getBmi()) + 
                         ") indicates you are underweight. " + metrics.getBmiCategory().getHealthAdvice();
                break;
            case OVERWEIGHT:
                severity = "WARNING";
                message = "Your BMI (" + String.format("%.1f", metrics.getBmi()) + 
                         ") indicates you are overweight. " + metrics.getBmiCategory().getHealthAdvice();
                break;
            case OBESE:
                severity = "CRITICAL";
                message = "Your BMI (" + String.format("%.1f", metrics.getBmi()) + 
                         ") indicates obesity. " + metrics.getBmiCategory().getHealthAdvice();
                break;
            default:
                return; // No alert needed for normal BMI
        }
        
        HealthAlert alert = new HealthAlert(userId, alertType, message, severity);
        healthAlertDAO.create(alert);
    }
}
