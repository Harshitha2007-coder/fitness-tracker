package com.fitnesstracker.service;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;
import com.fitnesstracker.util.BMICalculator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Service class for trainer operations (monitoring clients, analyzing trends, creating plans).
 */
public class TrainerService {
    private final UserDAO userDAO;
    private final StepsLogDAO stepsLogDAO;
    private final CaloriesLogDAO caloriesLogDAO;
    private final WorkoutDAO workoutDAO;
    private final HealthMetricsDAO healthMetricsDAO;
    private final GoalDAO goalDAO;
    private final AlertDAO alertDAO;
    private final TrainerPlanDAO trainerPlanDAO;

    public TrainerService() {
        this.userDAO = new UserDAO();
        this.stepsLogDAO = new StepsLogDAO();
        this.caloriesLogDAO = new CaloriesLogDAO();
        this.workoutDAO = new WorkoutDAO();
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.goalDAO = new GoalDAO();
        this.alertDAO = new AlertDAO();
        this.trainerPlanDAO = new TrainerPlanDAO();
    }

    // ==================== Client Management ====================

    /**
     * Get all clients assigned to a trainer.
     */
    public List<User> getClients(int trainerId) throws SQLException {
        return userDAO.getClientsForTrainer(trainerId);
    }

    /**
     * Assign a client to a trainer.
     */
    public void assignClient(int trainerId, int clientId) throws SQLException {
        // Verify trainer role
        Optional<User> trainerOpt = userDAO.findById(trainerId);
        if (trainerOpt.isEmpty() || trainerOpt.get().getRole() != Role.TRAINER) {
            throw new IllegalArgumentException("Invalid trainer ID");
        }
        
        // Verify client role
        Optional<User> clientOpt = userDAO.findById(clientId);
        if (clientOpt.isEmpty() || clientOpt.get().getRole() != Role.INDIVIDUAL) {
            throw new IllegalArgumentException("Invalid client ID");
        }
        
        userDAO.assignClientToTrainer(trainerId, clientId);
        
        // Notify client about assignment
        String trainerName = trainerOpt.get().getFullName();
        alertDAO.create(new Alert(clientId, "TRAINER_ASSIGNED", 
            "You have been assigned to trainer: " + trainerName + ". Welcome to your fitness journey!"));
    }

    /**
     * Remove a client from a trainer.
     */
    public void removeClient(int trainerId, int clientId) throws SQLException {
        userDAO.removeClientFromTrainer(trainerId, clientId);
    }

    // ==================== Progress Monitoring ====================

    /**
     * Get client's steps progress.
     */
    public Map<String, Object> getClientStepsProgress(int clientId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Object> progress = new HashMap<>();
        
        List<StepsLog> logs = stepsLogDAO.findByUserIdAndDateRange(clientId, startDate, endDate);
        int totalSteps = stepsLogDAO.getTotalSteps(clientId, startDate, endDate);
        double averageSteps = stepsLogDAO.getAverageSteps(clientId, startDate, endDate);
        
        progress.put("logs", logs);
        progress.put("totalSteps", totalSteps);
        progress.put("averageSteps", averageSteps);
        
        // Find best and worst days
        if (!logs.isEmpty()) {
            StepsLog bestDay = Collections.max(logs, Comparator.comparingInt(StepsLog::getSteps));
            StepsLog worstDay = Collections.min(logs, Comparator.comparingInt(StepsLog::getSteps));
            progress.put("bestDay", bestDay);
            progress.put("worstDay", worstDay);
        }
        
        return progress;
    }

    /**
     * Get client's calories progress.
     */
    public Map<String, Object> getClientCaloriesProgress(int clientId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Object> progress = new HashMap<>();
        
        List<CaloriesLog> logs = caloriesLogDAO.findByUserIdAndDateRange(clientId, startDate, endDate);
        int totalConsumed = caloriesLogDAO.getTotalCaloriesConsumed(clientId, startDate, endDate);
        int totalBurned = caloriesLogDAO.getTotalCaloriesBurned(clientId, startDate, endDate);
        
        progress.put("logs", logs);
        progress.put("totalCaloriesConsumed", totalConsumed);
        progress.put("totalCaloriesBurned", totalBurned);
        progress.put("netCalories", totalConsumed - totalBurned);
        
        // Calculate daily averages
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        progress.put("averageCaloriesConsumed", (double) totalConsumed / days);
        progress.put("averageCaloriesBurned", (double) totalBurned / days);
        
        return progress;
    }

    /**
     * Get client's workout progress.
     */
    public Map<String, Object> getClientWorkoutProgress(int clientId, LocalDate startDate, LocalDate endDate) throws SQLException {
        Map<String, Object> progress = new HashMap<>();
        
        List<Workout> workouts = workoutDAO.findByUserIdAndDateRange(clientId, startDate, endDate);
        int totalDuration = workoutDAO.getTotalDuration(clientId, startDate, endDate);
        int totalCaloriesBurned = workoutDAO.getTotalCaloriesBurned(clientId, startDate, endDate);
        int workoutCount = workoutDAO.getWorkoutCount(clientId, startDate, endDate);
        
        progress.put("workouts", workouts);
        progress.put("totalDuration", totalDuration);
        progress.put("totalCaloriesBurned", totalCaloriesBurned);
        progress.put("workoutCount", workoutCount);
        
        // Calculate average workout duration
        if (workoutCount > 0) {
            progress.put("averageWorkoutDuration", (double) totalDuration / workoutCount);
        }
        
        // Workout type breakdown
        Map<String, Integer> workoutTypeCount = new HashMap<>();
        Map<String, Integer> workoutTypeDuration = new HashMap<>();
        for (Workout workout : workouts) {
            String type = workout.getWorkoutType();
            workoutTypeCount.merge(type, 1, Integer::sum);
            workoutTypeDuration.merge(type, workout.getDurationMinutes(), Integer::sum);
        }
        progress.put("workoutTypeCount", workoutTypeCount);
        progress.put("workoutTypeDuration", workoutTypeDuration);
        
        return progress;
    }

    /**
     * Get client's health metrics.
     */
    public Map<String, Object> getClientHealthMetrics(int clientId) throws SQLException {
        Map<String, Object> metrics = new HashMap<>();
        
        Optional<User> userOpt = userDAO.findById(clientId);
        Optional<HealthMetrics> latestMetrics = healthMetricsDAO.findLatestByUserId(clientId);
        List<HealthMetrics> history = healthMetricsDAO.findByUserId(clientId);
        
        metrics.put("user", userOpt.orElse(null));
        metrics.put("latestMetrics", latestMetrics.orElse(null));
        metrics.put("metricsHistory", history);
        
        // Calculate weight change if history exists
        if (history.size() >= 2) {
            HealthMetrics latest = history.get(0);
            HealthMetrics oldest = history.get(history.size() - 1);
            double weightChange = latest.getWeightKg() - oldest.getWeightKg();
            double bmiChange = latest.getBmi() - oldest.getBmi();
            metrics.put("weightChange", weightChange);
            metrics.put("bmiChange", bmiChange);
        }
        
        // Add ideal weight recommendation
        if (userOpt.isPresent() && userOpt.get().getHeightCm() != null) {
            User user = userOpt.get();
            boolean isMale = user.getGender() == Gender.MALE;
            double idealWeight = BMICalculator.calculateIdealWeight(user.getHeightCm(), isMale);
            double[] weightRange = BMICalculator.getRecommendedWeightRange(user.getHeightCm());
            metrics.put("idealWeight", idealWeight);
            metrics.put("recommendedWeightRange", weightRange);
        }
        
        return metrics;
    }

    /**
     * Get client's goals progress.
     */
    public List<Goal> getClientGoals(int clientId) throws SQLException {
        return goalDAO.findByUserId(clientId);
    }

    // ==================== Trend Analysis ====================

    /**
     * Analyze fitness trends for a client.
     */
    public Map<String, Object> analyzeTrends(int clientId, int weeks) throws SQLException {
        Map<String, Object> trends = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(weeks);
        
        // Weekly breakdown
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        for (int i = 0; i < weeks; i++) {
            LocalDate weekStart = startDate.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);
            
            Map<String, Object> weekData = new HashMap<>();
            weekData.put("weekStart", weekStart);
            weekData.put("weekEnd", weekEnd);
            weekData.put("totalSteps", stepsLogDAO.getTotalSteps(clientId, weekStart, weekEnd));
            weekData.put("avgSteps", stepsLogDAO.getAverageSteps(clientId, weekStart, weekEnd));
            weekData.put("totalWorkoutDuration", workoutDAO.getTotalDuration(clientId, weekStart, weekEnd));
            weekData.put("workoutCount", workoutDAO.getWorkoutCount(clientId, weekStart, weekEnd));
            weekData.put("caloriesConsumed", caloriesLogDAO.getTotalCaloriesConsumed(clientId, weekStart, weekEnd));
            weekData.put("caloriesBurned", caloriesLogDAO.getTotalCaloriesBurned(clientId, weekStart, weekEnd));
            
            weeklyData.add(weekData);
        }
        trends.put("weeklyData", weeklyData);
        
        // Calculate overall trends
        if (weeklyData.size() >= 2) {
            Map<String, Object> firstWeek = weeklyData.get(0);
            Map<String, Object> lastWeek = weeklyData.get(weeklyData.size() - 1);
            
            int stepsFirst = (int) firstWeek.get("totalSteps");
            int stepsLast = (int) lastWeek.get("totalSteps");
            trends.put("stepsTrend", stepsLast > stepsFirst ? "IMPROVING" : (stepsLast < stepsFirst ? "DECLINING" : "STABLE"));
            
            int workoutsFirst = (int) firstWeek.get("workoutCount");
            int workoutsLast = (int) lastWeek.get("workoutCount");
            trends.put("workoutTrend", workoutsLast > workoutsFirst ? "IMPROVING" : (workoutsLast < workoutsFirst ? "DECLINING" : "STABLE"));
        }
        
        return trends;
    }

    // ==================== Plan Creation ====================

    /**
     * Create a workout plan for a client.
     */
    public TrainerPlan createWorkoutPlan(int trainerId, int clientId, String title, String description) throws SQLException {
        TrainerPlan plan = new TrainerPlan(trainerId, clientId, PlanType.WORKOUT, title, description);
        TrainerPlan savedPlan = trainerPlanDAO.create(plan);
        
        // Notify client
        alertDAO.create(new Alert(clientId, "NEW_PLAN", 
            "Your trainer has created a new workout plan for you: " + title));
        
        return savedPlan;
    }

    /**
     * Create a diet plan for a client.
     */
    public TrainerPlan createDietPlan(int trainerId, int clientId, String title, String description) throws SQLException {
        TrainerPlan plan = new TrainerPlan(trainerId, clientId, PlanType.DIET, title, description);
        TrainerPlan savedPlan = trainerPlanDAO.create(plan);
        
        // Notify client
        alertDAO.create(new Alert(clientId, "NEW_PLAN", 
            "Your trainer has created a new diet plan for you: " + title));
        
        return savedPlan;
    }

    /**
     * Create a general suggestion for a client.
     */
    public TrainerPlan createGeneralSuggestion(int trainerId, int clientId, String title, String description) throws SQLException {
        TrainerPlan plan = new TrainerPlan(trainerId, clientId, PlanType.GENERAL, title, description);
        TrainerPlan savedPlan = trainerPlanDAO.create(plan);
        
        // Notify client
        alertDAO.create(new Alert(clientId, "NEW_SUGGESTION", 
            "Your trainer has a new suggestion for you: " + title));
        
        return savedPlan;
    }

    /**
     * Get all plans created by a trainer.
     */
    public List<TrainerPlan> getCreatedPlans(int trainerId) throws SQLException {
        return trainerPlanDAO.findByTrainerId(trainerId);
    }

    /**
     * Update a plan.
     */
    public void updatePlan(TrainerPlan plan) throws SQLException {
        trainerPlanDAO.update(plan);
    }

    /**
     * Delete a plan.
     */
    public void deletePlan(int planId) throws SQLException {
        trainerPlanDAO.delete(planId);
    }

    // ==================== Goal Management for Clients ====================

    /**
     * Create a goal for a client.
     */
    public Goal createGoalForClient(int trainerId, int clientId, GoalType goalType, 
                                    double targetValue, LocalDate startDate, LocalDate endDate) throws SQLException {
        Goal goal = new Goal(clientId, goalType, targetValue, startDate, endDate);
        Goal savedGoal = goalDAO.create(goal);
        
        // Notify client
        alertDAO.create(new Alert(clientId, "NEW_GOAL", 
            "Your trainer has set a new " + goalType + " goal for you: " + targetValue));
        
        return savedGoal;
    }

    // ==================== Dashboard Summary ====================

    /**
     * Get summary for trainer dashboard.
     */
    public Map<String, Object> getDashboardSummary(int trainerId) throws SQLException {
        Map<String, Object> summary = new HashMap<>();
        
        List<User> clients = userDAO.getClientsForTrainer(trainerId);
        summary.put("totalClients", clients.size());
        summary.put("clients", clients);
        
        // Count clients by BMI category
        Map<String, Integer> bmiCategories = new HashMap<>();
        int activeGoals = 0;
        
        for (User client : clients) {
            Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(client.getId());
            if (metricsOpt.isPresent()) {
                String category = metricsOpt.get().getHealthClassification();
                bmiCategories.merge(category, 1, Integer::sum);
            }
            
            activeGoals += goalDAO.findActiveByUserId(client.getId()).size();
        }
        
        summary.put("bmiCategories", bmiCategories);
        summary.put("totalActiveGoals", activeGoals);
        
        // Recent plans created
        List<TrainerPlan> recentPlans = trainerPlanDAO.findByTrainerId(trainerId);
        summary.put("recentPlans", recentPlans.size() > 5 ? recentPlans.subList(0, 5) : recentPlans);
        
        return summary;
    }
}
