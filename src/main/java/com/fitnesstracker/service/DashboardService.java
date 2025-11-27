package com.fitnesstracker.service;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service class for generating dashboard data.
 */
public class DashboardService {
    private final UserDAO userDAO;
    private final StepsLogDAO stepsLogDAO;
    private final CaloriesLogDAO caloriesLogDAO;
    private final WorkoutDAO workoutDAO;
    private final HealthMetricsDAO healthMetricsDAO;
    private final GoalDAO goalDAO;
    private final AlertDAO alertDAO;
    private final TrainerPlanDAO trainerPlanDAO;

    public DashboardService() {
        this.userDAO = new UserDAO();
        this.stepsLogDAO = new StepsLogDAO();
        this.caloriesLogDAO = new CaloriesLogDAO();
        this.workoutDAO = new WorkoutDAO();
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.goalDAO = new GoalDAO();
        this.alertDAO = new AlertDAO();
        this.trainerPlanDAO = new TrainerPlanDAO();
    }

    /**
     * Generate dashboard data for an individual user.
     */
    public Map<String, Object> getIndividualDashboard(int userId) throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.minusDays(29);

        // User profile
        Optional<User> userOpt = userDAO.findById(userId);
        dashboard.put("user", userOpt.orElse(null));

        // Today's stats
        Map<String, Object> todayStats = new HashMap<>();
        Optional<StepsLog> todaySteps = stepsLogDAO.findByUserIdAndDate(userId, today);
        todayStats.put("steps", todaySteps.map(StepsLog::getSteps).orElse(0));
        
        Optional<CaloriesLog> todayCalories = caloriesLogDAO.findByUserIdAndDate(userId, today);
        todayStats.put("caloriesConsumed", todayCalories.map(CaloriesLog::getCaloriesConsumed).orElse(0));
        todayStats.put("caloriesBurned", todayCalories.map(CaloriesLog::getCaloriesBurned).orElse(0));
        
        List<Workout> todayWorkouts = workoutDAO.findByUserIdAndDateRange(userId, today, today);
        int todayWorkoutDuration = todayWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum();
        todayStats.put("workoutDuration", todayWorkoutDuration);
        todayStats.put("workoutCount", todayWorkouts.size());
        
        dashboard.put("today", todayStats);

        // Weekly summary
        Map<String, Object> weeklySummary = new HashMap<>();
        weeklySummary.put("totalSteps", stepsLogDAO.getTotalSteps(userId, weekStart, today));
        weeklySummary.put("avgSteps", stepsLogDAO.getAverageSteps(userId, weekStart, today));
        weeklySummary.put("totalWorkoutDuration", workoutDAO.getTotalDuration(userId, weekStart, today));
        weeklySummary.put("workoutCount", workoutDAO.getWorkoutCount(userId, weekStart, today));
        weeklySummary.put("caloriesConsumed", caloriesLogDAO.getTotalCaloriesConsumed(userId, weekStart, today));
        weeklySummary.put("caloriesBurned", caloriesLogDAO.getTotalCaloriesBurned(userId, weekStart, today));
        dashboard.put("weekly", weeklySummary);

        // Monthly summary
        Map<String, Object> monthlySummary = new HashMap<>();
        monthlySummary.put("totalSteps", stepsLogDAO.getTotalSteps(userId, monthStart, today));
        monthlySummary.put("avgSteps", stepsLogDAO.getAverageSteps(userId, monthStart, today));
        monthlySummary.put("totalWorkoutDuration", workoutDAO.getTotalDuration(userId, monthStart, today));
        monthlySummary.put("workoutCount", workoutDAO.getWorkoutCount(userId, monthStart, today));
        monthlySummary.put("caloriesConsumed", caloriesLogDAO.getTotalCaloriesConsumed(userId, monthStart, today));
        monthlySummary.put("caloriesBurned", caloriesLogDAO.getTotalCaloriesBurned(userId, monthStart, today));
        dashboard.put("monthly", monthlySummary);

        // Health metrics
        Optional<HealthMetrics> latestMetrics = healthMetricsDAO.findLatestByUserId(userId);
        dashboard.put("healthMetrics", latestMetrics.orElse(null));

        // Active goals with progress
        List<Goal> activeGoals = goalDAO.findActiveByUserId(userId);
        dashboard.put("activeGoals", activeGoals);

        // Unread alerts
        List<Alert> unreadAlerts = alertDAO.findUnreadByUserId(userId);
        dashboard.put("unreadAlerts", unreadAlerts);
        dashboard.put("unreadAlertCount", unreadAlerts.size());

        // Recent trainer plans
        List<TrainerPlan> recentPlans = trainerPlanDAO.findByClientId(userId);
        dashboard.put("trainerPlans", recentPlans.size() > 5 ? recentPlans.subList(0, 5) : recentPlans);

        // Weekly steps chart data
        List<Map<String, Object>> weeklyStepsChart = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", date);
            Optional<StepsLog> steps = stepsLogDAO.findByUserIdAndDate(userId, date);
            dataPoint.put("steps", steps.map(StepsLog::getSteps).orElse(0));
            weeklyStepsChart.add(dataPoint);
        }
        dashboard.put("weeklyStepsChart", weeklyStepsChart);

        return dashboard;
    }

    /**
     * Generate dashboard data for a trainer.
     */
    public Map<String, Object> getTrainerDashboard(int trainerId) throws SQLException {
        Map<String, Object> dashboard = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        // Trainer profile
        Optional<User> trainerOpt = userDAO.findById(trainerId);
        dashboard.put("trainer", trainerOpt.orElse(null));

        // Clients list with summary
        List<User> clients = userDAO.getClientsForTrainer(trainerId);
        List<Map<String, Object>> clientSummaries = new ArrayList<>();

        int totalActiveGoals = 0;
        Map<String, Integer> bmiCategories = new HashMap<>();

        for (User client : clients) {
            Map<String, Object> clientSummary = new HashMap<>();
            clientSummary.put("client", client);

            // Weekly activity
            int weeklySteps = stepsLogDAO.getTotalSteps(client.getId(), weekStart, today);
            int weeklyWorkouts = workoutDAO.getWorkoutCount(client.getId(), weekStart, today);
            clientSummary.put("weeklySteps", weeklySteps);
            clientSummary.put("weeklyWorkouts", weeklyWorkouts);

            // Latest BMI
            Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(client.getId());
            if (metrics.isPresent()) {
                clientSummary.put("bmi", metrics.get().getBmi());
                clientSummary.put("healthClassification", metrics.get().getHealthClassification());
                bmiCategories.merge(metrics.get().getHealthClassification(), 1, Integer::sum);
            }

            // Active goals count
            List<Goal> clientActiveGoals = goalDAO.findActiveByUserId(client.getId());
            clientSummary.put("activeGoals", clientActiveGoals.size());
            totalActiveGoals += clientActiveGoals.size();

            clientSummaries.add(clientSummary);
        }

        dashboard.put("clients", clientSummaries);
        dashboard.put("totalClients", clients.size());
        dashboard.put("totalActiveGoals", totalActiveGoals);
        dashboard.put("bmiCategories", bmiCategories);

        // Recent plans created
        List<TrainerPlan> allPlans = trainerPlanDAO.findByTrainerId(trainerId);
        dashboard.put("totalPlans", allPlans.size());
        dashboard.put("recentPlans", allPlans.size() > 5 ? allPlans.subList(0, 5) : allPlans);

        // Clients needing attention (low activity or unhealthy BMI)
        List<Map<String, Object>> clientsNeedingAttention = new ArrayList<>();
        for (Map<String, Object> summary : clientSummaries) {
            int weeklySteps = (int) summary.get("weeklySteps");
            int weeklyWorkouts = (int) summary.get("weeklyWorkouts");
            String classification = (String) summary.getOrDefault("healthClassification", "Unknown");

            if (weeklySteps < 35000 || weeklyWorkouts < 2 || 
                !classification.equals("Normal") && !classification.equals("Unknown")) {
                clientsNeedingAttention.add(summary);
            }
        }
        dashboard.put("clientsNeedingAttention", clientsNeedingAttention);

        return dashboard;
    }

    /**
     * Generate a quick status summary.
     */
    public String generateQuickSummary(int userId, Role role) throws SQLException {
        StringBuilder summary = new StringBuilder();

        if (role == Role.INDIVIDUAL) {
            LocalDate today = LocalDate.now();
            Optional<StepsLog> todaySteps = stepsLogDAO.findByUserIdAndDate(userId, today);
            int steps = todaySteps.map(StepsLog::getSteps).orElse(0);
            
            List<Workout> todayWorkouts = workoutDAO.findByUserIdAndDateRange(userId, today, today);
            int workoutMinutes = todayWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum();

            summary.append("📊 Today's Progress:\n");
            summary.append(String.format("   👟 Steps: %,d\n", steps));
            summary.append(String.format("   🏋️ Workout: %d minutes (%d sessions)\n", workoutMinutes, todayWorkouts.size()));

            List<Goal> activeGoals = goalDAO.findActiveByUserId(userId);
            if (!activeGoals.isEmpty()) {
                summary.append(String.format("   🎯 Active Goals: %d\n", activeGoals.size()));
            }

            int unreadAlerts = alertDAO.getUnreadCount(userId);
            if (unreadAlerts > 0) {
                summary.append(String.format("   🔔 Unread Alerts: %d\n", unreadAlerts));
            }
        } else {
            List<User> clients = userDAO.getClientsForTrainer(userId);
            summary.append("📋 Trainer Overview:\n");
            summary.append(String.format("   👥 Total Clients: %d\n", clients.size()));

            int totalActiveGoals = 0;
            for (User client : clients) {
                totalActiveGoals += goalDAO.findActiveByUserId(client.getId()).size();
            }
            summary.append(String.format("   🎯 Total Active Goals: %d\n", totalActiveGoals));

            List<TrainerPlan> plans = trainerPlanDAO.findByTrainerId(userId);
            summary.append(String.format("   📝 Plans Created: %d\n", plans.size()));
        }

        return summary.toString();
    }
}
