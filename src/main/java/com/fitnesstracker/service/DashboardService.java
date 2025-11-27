package com.fitnesstracker.service;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for generating dashboard data and analytics.
 */
public class DashboardService {
    
    private final WorkoutDAO workoutDAO;
    private final DailyStepsDAO dailyStepsDAO;
    private final CalorieIntakeDAO calorieIntakeDAO;
    private final HealthMetricsDAO healthMetricsDAO;
    private final HealthAlertDAO healthAlertDAO;
    private final UserDAO userDAO;

    public DashboardService() {
        this.workoutDAO = new WorkoutDAO();
        this.dailyStepsDAO = new DailyStepsDAO();
        this.calorieIntakeDAO = new CalorieIntakeDAO();
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.healthAlertDAO = new HealthAlertDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Get dashboard summary for an individual user.
     */
    public Map<String, Object> getIndividualDashboard(int userId) throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        LocalDate monthAgo = today.minusDays(30);

        // Today's stats
        Optional<DailySteps> todaySteps = dailyStepsDAO.findByUserIdAndDate(userId, today);
        int todayStepCount = todaySteps.map(DailySteps::getStepCount).orElse(0);
        int todayCalories = calorieIntakeDAO.getTotalCaloriesForDate(userId, today);
        
        dashboard.put("todaySteps", todayStepCount);
        dashboard.put("todayCalories", todayCalories);
        
        // Weekly stats
        int weeklySteps = dailyStepsDAO.getTotalSteps(userId, weekAgo, today);
        int weeklyWorkouts = workoutDAO.getWorkoutCount(userId, weekAgo, today);
        int weeklyWorkoutDuration = workoutDAO.getTotalDuration(userId, weekAgo, today);
        int weeklyCaloriesBurned = workoutDAO.getTotalCaloriesBurned(userId, weekAgo, today);
        double avgDailySteps = dailyStepsDAO.getAverageSteps(userId, weekAgo, today);
        
        dashboard.put("weeklySteps", weeklySteps);
        dashboard.put("weeklyWorkouts", weeklyWorkouts);
        dashboard.put("weeklyWorkoutDuration", weeklyWorkoutDuration);
        dashboard.put("weeklyCaloriesBurned", weeklyCaloriesBurned);
        dashboard.put("avgDailySteps", avgDailySteps);
        
        // Step goal progress
        int daysGoalAchieved = dailyStepsDAO.getDaysGoalAchieved(userId, weekAgo, today);
        dashboard.put("daysGoalAchieved", daysGoalAchieved);
        
        // Latest health metrics
        Optional<HealthMetrics> latestMetrics = healthMetricsDAO.findLatestByUserId(userId);
        if (latestMetrics.isPresent()) {
            HealthMetrics metrics = latestMetrics.get();
            dashboard.put("currentWeight", metrics.getWeightKg());
            dashboard.put("currentBMI", metrics.getBmi());
            dashboard.put("bmiCategory", metrics.getBmiCategory().getDisplayName());
            dashboard.put("bmiAdvice", metrics.getBmiCategory().getHealthAdvice());
        }
        
        // Weight change over month
        double weightChange = healthMetricsDAO.getWeightChange(userId, monthAgo, today);
        dashboard.put("monthlyWeightChange", weightChange);
        
        // Recent workouts
        List<Workout> recentWorkouts = workoutDAO.findByUserIdAndDateRange(userId, weekAgo, today);
        dashboard.put("recentWorkouts", recentWorkouts);
        
        // Unread alerts
        List<HealthAlert> unreadAlerts = healthAlertDAO.findUnreadByUserId(userId);
        dashboard.put("unreadAlerts", unreadAlerts);
        dashboard.put("alertCount", unreadAlerts.size());
        
        return dashboard;
    }

    /**
     * Get dashboard summary for a trainer.
     */
    public Map<String, Object> getTrainerDashboard(int trainerId) throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);

        // Get assigned individuals
        List<User> individuals = userDAO.findIndividualsByTrainer(trainerId);
        dashboard.put("assignedIndividuals", individuals);
        dashboard.put("totalClients", individuals.size());
        
        // Client summaries
        Map<Integer, Map<String, Object>> clientSummaries = new HashMap<>();
        for (User individual : individuals) {
            Map<String, Object> summary = new HashMap<>();
            
            int weeklyWorkouts = workoutDAO.getWorkoutCount(individual.getId(), weekAgo, today);
            double avgSteps = dailyStepsDAO.getAverageSteps(individual.getId(), weekAgo, today);
            Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(individual.getId());
            
            summary.put("name", individual.getFullName());
            summary.put("weeklyWorkouts", weeklyWorkouts);
            summary.put("avgDailySteps", avgSteps);
            
            if (metrics.isPresent()) {
                summary.put("currentBMI", metrics.get().getBmi());
                summary.put("bmiCategory", metrics.get().getBmiCategory().getDisplayName());
                summary.put("needsAttention", metrics.get().getBmiCategory().needsAlert());
            }
            
            clientSummaries.put(individual.getId(), summary);
        }
        dashboard.put("clientSummaries", clientSummaries);
        
        // Clients needing attention (non-normal BMI)
        long clientsNeedingAttention = individuals.stream()
                .filter(i -> {
                    try {
                        Optional<HealthMetrics> m = healthMetricsDAO.findLatestByUserId(i.getId());
                        return m.isPresent() && m.get().getBmiCategory().needsAlert();
                    } catch (SQLException e) {
                        return false;
                    }
                })
                .count();
        dashboard.put("clientsNeedingAttention", clientsNeedingAttention);
        
        return dashboard;
    }

    /**
     * Get detailed progress report for a user.
     */
    public Map<String, Object> getProgressReport(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        Map<String, Object> report = new HashMap<>();
        
        // Steps analysis
        int totalSteps = dailyStepsDAO.getTotalSteps(userId, startDate, endDate);
        double avgSteps = dailyStepsDAO.getAverageSteps(userId, startDate, endDate);
        int daysGoalMet = dailyStepsDAO.getDaysGoalAchieved(userId, startDate, endDate);
        List<DailySteps> stepsHistory = dailyStepsDAO.findByUserIdAndDateRange(userId, startDate, endDate);
        
        report.put("totalSteps", totalSteps);
        report.put("avgDailySteps", avgSteps);
        report.put("daysStepGoalMet", daysGoalMet);
        report.put("stepsHistory", stepsHistory);
        
        // Workout analysis
        List<Workout> workouts = workoutDAO.findByUserIdAndDateRange(userId, startDate, endDate);
        int totalWorkoutDuration = workoutDAO.getTotalDuration(userId, startDate, endDate);
        int totalCaloriesBurned = workoutDAO.getTotalCaloriesBurned(userId, startDate, endDate);
        
        report.put("workouts", workouts);
        report.put("totalWorkoutCount", workouts.size());
        report.put("totalWorkoutDuration", totalWorkoutDuration);
        report.put("totalCaloriesBurned", totalCaloriesBurned);
        
        // Calorie intake analysis
        int totalCaloriesConsumed = calorieIntakeDAO.getTotalCalories(userId, startDate, endDate);
        double avgDailyCalories = calorieIntakeDAO.getAverageDailyCalories(userId, startDate, endDate);
        
        report.put("totalCaloriesConsumed", totalCaloriesConsumed);
        report.put("avgDailyCalories", avgDailyCalories);
        report.put("netCalories", totalCaloriesConsumed - totalCaloriesBurned);
        
        // Health metrics trend
        List<HealthMetrics> metricsHistory = healthMetricsDAO.findByUserIdAndDateRange(userId, startDate, endDate);
        double weightChange = healthMetricsDAO.getWeightChange(userId, startDate, endDate);
        
        report.put("healthMetricsHistory", metricsHistory);
        report.put("weightChange", weightChange);
        
        return report;
    }

    /**
     * Generate trend analysis for visualization.
     */
    public Map<String, List<?>> getTrendData(int userId, int days) throws SQLException {
        Map<String, List<?>> trends = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        trends.put("steps", dailyStepsDAO.findByUserIdAndDateRange(userId, startDate, endDate));
        trends.put("workouts", workoutDAO.findByUserIdAndDateRange(userId, startDate, endDate));
        trends.put("calories", calorieIntakeDAO.findByUserIdAndDateRange(userId, startDate, endDate));
        trends.put("healthMetrics", healthMetricsDAO.findByUserIdAndDateRange(userId, startDate, endDate));
        
        return trends;
    }
}
