package com.fitnesstracker.service;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;
import com.fitnesstracker.util.BMICalculator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for individual user operations (logging fitness data, viewing progress).
 */
public class IndividualService {
    private final UserDAO userDAO;
    private final StepsLogDAO stepsLogDAO;
    private final CaloriesLogDAO caloriesLogDAO;
    private final WorkoutDAO workoutDAO;
    private final HealthMetricsDAO healthMetricsDAO;
    private final GoalDAO goalDAO;
    private final AlertDAO alertDAO;
    private final TrainerPlanDAO trainerPlanDAO;

    public IndividualService() {
        this.userDAO = new UserDAO();
        this.stepsLogDAO = new StepsLogDAO();
        this.caloriesLogDAO = new CaloriesLogDAO();
        this.workoutDAO = new WorkoutDAO();
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.goalDAO = new GoalDAO();
        this.alertDAO = new AlertDAO();
        this.trainerPlanDAO = new TrainerPlanDAO();
    }

    // ==================== Steps Logging ====================

    /**
     * Log steps for a specific date.
     */
    public StepsLog logSteps(int userId, int steps, LocalDate date) throws SQLException {
        StepsLog log = new StepsLog(userId, steps, date);
        StepsLog savedLog = stepsLogDAO.logSteps(log);
        
        // Check if any step goals should be updated
        checkAndUpdateStepGoals(userId);
        
        return savedLog;
    }

    /**
     * Get steps logs for a date range.
     */
    public List<StepsLog> getStepsLogs(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return stepsLogDAO.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get total steps for a date range.
     */
    public int getTotalSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return stepsLogDAO.getTotalSteps(userId, startDate, endDate);
    }

    /**
     * Get average daily steps for a date range.
     */
    public double getAverageSteps(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return stepsLogDAO.getAverageSteps(userId, startDate, endDate);
    }

    // ==================== Calories Logging ====================

    /**
     * Log calories for a specific date.
     */
    public CaloriesLog logCalories(int userId, int caloriesConsumed, int caloriesBurned, LocalDate date) throws SQLException {
        CaloriesLog log = new CaloriesLog(userId, caloriesConsumed, caloriesBurned, date);
        return caloriesLogDAO.logCalories(log);
    }

    /**
     * Get calories logs for a date range.
     */
    public List<CaloriesLog> getCaloriesLogs(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return caloriesLogDAO.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get total calories consumed for a date range.
     */
    public int getTotalCaloriesConsumed(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return caloriesLogDAO.getTotalCaloriesConsumed(userId, startDate, endDate);
    }

    /**
     * Get total calories burned for a date range.
     */
    public int getTotalCaloriesBurned(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return caloriesLogDAO.getTotalCaloriesBurned(userId, startDate, endDate);
    }

    // ==================== Workout Logging ====================

    /**
     * Log a workout.
     */
    public Workout logWorkout(int userId, String workoutType, int durationMinutes, Integer caloriesBurned, 
                               Intensity intensity, String notes, LocalDate date) throws SQLException {
        Workout workout = new Workout(userId, workoutType, durationMinutes, date);
        workout.setCaloriesBurned(caloriesBurned);
        workout.setIntensity(intensity);
        workout.setNotes(notes);
        
        return workoutDAO.create(workout);
    }

    /**
     * Get workouts for a date range.
     */
    public List<Workout> getWorkouts(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return workoutDAO.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    /**
     * Get all workouts for a user.
     */
    public List<Workout> getAllWorkouts(int userId) throws SQLException {
        return workoutDAO.findByUserId(userId);
    }

    /**
     * Get total workout duration for a date range.
     */
    public int getTotalWorkoutDuration(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return workoutDAO.getTotalDuration(userId, startDate, endDate);
    }

    /**
     * Get workout count for a date range.
     */
    public int getWorkoutCount(int userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        return workoutDAO.getWorkoutCount(userId, startDate, endDate);
    }

    // ==================== Health Metrics ====================

    /**
     * Update user profile with height and weight, calculate BMI.
     */
    public HealthMetrics updateHealthMetrics(int userId, double heightCm, double weightKg) throws SQLException {
        // Update user profile
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        user.setHeightCm(heightCm);
        user.setWeightKg(weightKg);
        userDAO.update(user);

        // Calculate BMI and health classification
        double bmi = BMICalculator.calculateBMI(weightKg, heightCm);
        String classification = BMICalculator.getHealthClassification(bmi);
        
        // Save health metrics
        HealthMetrics metrics = new HealthMetrics(userId, bmi, classification, weightKg, heightCm);
        HealthMetrics savedMetrics = healthMetricsDAO.create(metrics);
        
        // Generate health alert based on BMI
        generateBMIAlert(userId, bmi, classification);
        
        return savedMetrics;
    }

    /**
     * Get latest health metrics for a user.
     */
    public Optional<HealthMetrics> getLatestHealthMetrics(int userId) throws SQLException {
        return healthMetricsDAO.findLatestByUserId(userId);
    }

    /**
     * Get health metrics history for a user.
     */
    public List<HealthMetrics> getHealthMetricsHistory(int userId) throws SQLException {
        return healthMetricsDAO.findByUserId(userId);
    }

    // ==================== Goals ====================

    /**
     * Create a new goal.
     */
    public Goal createGoal(int userId, GoalType goalType, double targetValue, LocalDate startDate, LocalDate endDate) throws SQLException {
        Goal goal = new Goal(userId, goalType, targetValue, startDate, endDate);
        return goalDAO.create(goal);
    }

    /**
     * Get active goals for a user.
     */
    public List<Goal> getActiveGoals(int userId) throws SQLException {
        return goalDAO.findActiveByUserId(userId);
    }

    /**
     * Get all goals for a user.
     */
    public List<Goal> getAllGoals(int userId) throws SQLException {
        return goalDAO.findByUserId(userId);
    }

    /**
     * Update goal progress manually.
     */
    public void updateGoalProgress(int goalId, double currentValue) throws SQLException {
        goalDAO.updateProgress(goalId, currentValue);
        
        // Check if goal is completed
        Optional<Goal> goalOpt = goalDAO.findById(goalId);
        if (goalOpt.isPresent()) {
            Goal goal = goalOpt.get();
            if (currentValue >= goal.getTargetValue()) {
                goalDAO.updateStatus(goalId, GoalStatus.COMPLETED);
                // Generate congratulation alert
                alertDAO.create(new Alert(goal.getUserId(), "GOAL_COMPLETED", 
                    "Congratulations! You've completed your " + goal.getGoalType() + " goal!"));
            }
        }
    }

    // ==================== Alerts ====================

    /**
     * Get unread alerts for a user.
     */
    public List<Alert> getUnreadAlerts(int userId) throws SQLException {
        return alertDAO.findUnreadByUserId(userId);
    }

    /**
     * Get all alerts for a user.
     */
    public List<Alert> getAllAlerts(int userId) throws SQLException {
        return alertDAO.findByUserId(userId);
    }

    /**
     * Mark alert as read.
     */
    public void markAlertAsRead(int alertId) throws SQLException {
        alertDAO.markAsRead(alertId);
    }

    /**
     * Mark all alerts as read.
     */
    public void markAllAlertsAsRead(int userId) throws SQLException {
        alertDAO.markAllAsRead(userId);
    }

    /**
     * Get unread alert count.
     */
    public int getUnreadAlertCount(int userId) throws SQLException {
        return alertDAO.getUnreadCount(userId);
    }

    // ==================== Trainer Plans ====================

    /**
     * Get trainer plans for a user.
     */
    public List<TrainerPlan> getTrainerPlans(int userId) throws SQLException {
        return trainerPlanDAO.findByClientId(userId);
    }

    /**
     * Get trainer plans by type.
     */
    public List<TrainerPlan> getTrainerPlansByType(int userId, PlanType planType) throws SQLException {
        return trainerPlanDAO.findByClientIdAndType(userId, planType);
    }

    // ==================== User Profile ====================

    /**
     * Get user profile.
     */
    public Optional<User> getUserProfile(int userId) throws SQLException {
        return userDAO.findById(userId);
    }

    /**
     * Update user profile.
     */
    public void updateUserProfile(User user) throws SQLException {
        userDAO.update(user);
    }

    // ==================== Private Helper Methods ====================

    /**
     * Generate BMI-based health alert.
     */
    private void generateBMIAlert(int userId, double bmi, String classification) throws SQLException {
        String alertType = "BMI_" + classification.toUpperCase().replace(" ", "_");
        String message;
        
        if (classification.equals("Underweight")) {
            message = String.format("Your BMI is %.1f (Underweight). Consider consulting a nutritionist for a healthy diet plan.", bmi);
        } else if (classification.equals("Normal")) {
            message = String.format("Great job! Your BMI is %.1f (Normal). Keep maintaining your healthy lifestyle!", bmi);
        } else if (classification.equals("Overweight")) {
            message = String.format("Your BMI is %.1f (Overweight). Consider increasing physical activity and reviewing your diet.", bmi);
        } else {
            message = String.format("Your BMI is %.1f (%s). Please consult a healthcare professional for personalized guidance.", bmi, classification);
        }
        
        alertDAO.create(new Alert(userId, alertType, message));
    }

    /**
     * Check and update step goals based on current progress.
     */
    private void checkAndUpdateStepGoals(int userId) throws SQLException {
        List<Goal> activeGoals = goalDAO.findActiveByUserId(userId);
        LocalDate today = LocalDate.now();
        
        for (Goal goal : activeGoals) {
            if (goal.getGoalType() == GoalType.STEPS) {
                int totalSteps = stepsLogDAO.getTotalSteps(userId, goal.getStartDate(), today);
                goalDAO.updateProgress(goal.getId(), totalSteps);
                
                if (totalSteps >= goal.getTargetValue()) {
                    goalDAO.updateStatus(goal.getId(), GoalStatus.COMPLETED);
                    alertDAO.create(new Alert(userId, "GOAL_COMPLETED", 
                        "Congratulations! You've completed your steps goal of " + (int) goal.getTargetValue() + " steps!"));
                }
            }
        }
    }
}
