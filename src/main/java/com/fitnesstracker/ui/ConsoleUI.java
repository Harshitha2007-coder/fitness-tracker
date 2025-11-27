package com.fitnesstracker.ui;

import com.fitnesstracker.chatbot.FitnessChatbot;
import com.fitnesstracker.model.*;
import com.fitnesstracker.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Console-based user interface for the Fitness Tracker application.
 * Provides interactive menus for both individuals and trainers.
 */
public class ConsoleUI {
    private final Scanner scanner;
    private final AuthService authService;
    private final IndividualService individualService;
    private final TrainerService trainerService;
    private final DashboardService dashboardService;
    private final FitnessChatbot chatbot;
    
    private User currentUser;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthService();
        this.individualService = new IndividualService();
        this.trainerService = new TrainerService();
        this.dashboardService = new DashboardService();
        this.chatbot = new FitnessChatbot();
    }

    /**
     * Start the application.
     */
    public void start() {
        printWelcomeBanner();
        
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                if (currentUser.getRole() == Role.INDIVIDUAL) {
                    showIndividualMenu();
                } else {
                    showTrainerMenu();
                }
            }
        }
    }

    /**
     * Print welcome banner.
     */
    private void printWelcomeBanner() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    FITNESS TRACKER                            ║");
        System.out.println("║           Your Personal Health & Fitness Companion            ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Show login menu.
     */
    private void showLoginMenu() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("            WELCOME MENU");
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. Login");
        System.out.println("2. Register as Individual");
        System.out.println("3. Register as Trainer");
        System.out.println("0. Exit");
        System.out.print("\nSelect option: ");

        int choice = readInt();
        switch (choice) {
            case 1 -> handleLogin();
            case 2 -> handleRegistration(Role.INDIVIDUAL);
            case 3 -> handleRegistration(Role.TRAINER);
            case 0 -> {
                System.out.println("\nThank you for using Fitness Tracker. Stay healthy! 💪");
                System.exit(0);
            }
            default -> System.out.println("Invalid option. Please try again.");
        }
    }

    /**
     * Handle user login.
     */
    private void handleLogin() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("              LOGIN");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            Optional<User> userOpt = authService.login(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("\n✓ Login successful! Welcome, " + currentUser.getFullName() + "!");
                
                // Show quick summary
                String summary = dashboardService.generateQuickSummary(currentUser.getId(), currentUser.getRole());
                System.out.println("\n" + summary);
            } else {
                System.out.println("\n✗ Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("\n✗ Login failed: " + e.getMessage());
        }
    }

    /**
     * Handle user registration.
     */
    private void handleRegistration(Role role) {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          REGISTRATION (" + role + ")");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password (min 8 chars, 1 upper, 1 lower, 1 digit): ");
        String password = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine().trim();
        
        try {
            User user = authService.register(username, password, email, fullName, role);
            currentUser = user;
            System.out.println("\n✓ Registration successful! Welcome, " + fullName + "!");
            
            if (role == Role.INDIVIDUAL) {
                System.out.println("\nWould you like to set up your profile now? (y/n): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    handleUpdateProfile();
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ Registration failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\n✗ Registration failed: " + e.getMessage());
        }
    }

    /**
     * Show individual user menu.
     */
    private void showIndividualMenu() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        INDIVIDUAL DASHBOARD");
        System.out.println("        User: " + currentUser.getFullName());
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. 📊 View Dashboard");
        System.out.println("2. 👟 Log Steps");
        System.out.println("3. 🔥 Log Calories");
        System.out.println("4. 🏋️ Log Workout");
        System.out.println("5. 📈 View Progress History");
        System.out.println("6. ⚖️ Update Health Metrics (Weight/Height)");
        System.out.println("7. 🎯 View Goals");
        System.out.println("8. 🔔 View Alerts");
        System.out.println("9. 📝 View Trainer Plans");
        System.out.println("10. 🤖 AI Fitness Chatbot");
        System.out.println("11. 👤 Update Profile");
        System.out.println("0. Logout");
        System.out.print("\nSelect option: ");

        int choice = readInt();
        try {
            switch (choice) {
                case 1 -> viewIndividualDashboard();
                case 2 -> logSteps();
                case 3 -> logCalories();
                case 4 -> logWorkout();
                case 5 -> viewProgressHistory();
                case 6 -> updateHealthMetrics();
                case 7 -> viewGoals();
                case 8 -> viewAlerts();
                case 9 -> viewTrainerPlans();
                case 10 -> startChatbot();
                case 11 -> handleUpdateProfile();
                case 0 -> logout();
                default -> System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    /**
     * Show trainer menu.
     */
    private void showTrainerMenu() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         TRAINER DASHBOARD");
        System.out.println("         User: " + currentUser.getFullName());
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. 📊 View Dashboard");
        System.out.println("2. 👥 View Clients");
        System.out.println("3. 📈 View Client Progress");
        System.out.println("4. 📉 Analyze Client Trends");
        System.out.println("5. 📝 Create Workout Plan");
        System.out.println("6. 🥗 Create Diet Plan");
        System.out.println("7. 💡 Create Suggestion");
        System.out.println("8. 🎯 Set Goal for Client");
        System.out.println("9. ➕ Assign New Client");
        System.out.println("10. 📋 View Created Plans");
        System.out.println("11. 🤖 AI Fitness Chatbot");
        System.out.println("0. Logout");
        System.out.print("\nSelect option: ");

        int choice = readInt();
        try {
            switch (choice) {
                case 1 -> viewTrainerDashboard();
                case 2 -> viewClients();
                case 3 -> viewClientProgress();
                case 4 -> analyzeClientTrends();
                case 5 -> createWorkoutPlan();
                case 6 -> createDietPlan();
                case 7 -> createSuggestion();
                case 8 -> setGoalForClient();
                case 9 -> assignClient();
                case 10 -> viewCreatedPlans();
                case 11 -> startChatbot();
                case 0 -> logout();
                default -> System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
        }
    }

    // ==================== Individual Functions ====================

    private void viewIndividualDashboard() throws SQLException {
        Map<String, Object> dashboard = dashboardService.getIndividualDashboard(currentUser.getId());
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    YOUR FITNESS DASHBOARD                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        // Today's stats
        @SuppressWarnings("unchecked")
        Map<String, Object> today = (Map<String, Object>) dashboard.get("today");
        System.out.println("\n📅 TODAY'S SUMMARY:");
        System.out.println("   👟 Steps: " + String.format("%,d", today.get("steps")));
        System.out.println("   🔥 Calories consumed: " + String.format("%,d", today.get("caloriesConsumed")));
        System.out.println("   💪 Calories burned: " + String.format("%,d", today.get("caloriesBurned")));
        System.out.println("   🏋️ Workout: " + today.get("workoutDuration") + " min (" + today.get("workoutCount") + " sessions)");
        
        // Weekly summary
        @SuppressWarnings("unchecked")
        Map<String, Object> weekly = (Map<String, Object>) dashboard.get("weekly");
        System.out.println("\n📊 THIS WEEK:");
        System.out.println("   👟 Total Steps: " + String.format("%,d", weekly.get("totalSteps")));
        System.out.println("   📈 Avg Steps/Day: " + String.format("%,.0f", weekly.get("avgSteps")));
        System.out.println("   🏋️ Workouts: " + weekly.get("workoutCount") + " sessions, " + weekly.get("totalWorkoutDuration") + " min");
        
        // Health metrics
        HealthMetrics metrics = (HealthMetrics) dashboard.get("healthMetrics");
        if (metrics != null) {
            System.out.println("\n⚖️ HEALTH STATUS:");
            System.out.println("   BMI: " + String.format("%.1f", metrics.getBmi()) + " (" + metrics.getHealthClassification() + ")");
        }
        
        // Active goals
        @SuppressWarnings("unchecked")
        List<Goal> goals = (List<Goal>) dashboard.get("activeGoals");
        if (goals != null && !goals.isEmpty()) {
            System.out.println("\n🎯 ACTIVE GOALS:");
            for (Goal goal : goals) {
                String progressBar = getProgressBar(goal.getProgressPercentage());
                System.out.println("   " + goal.getGoalType() + ": " + progressBar + " " + 
                    String.format("%.1f%%", goal.getProgressPercentage()));
            }
        }
        
        // Alerts
        int alertCount = (int) dashboard.get("unreadAlertCount");
        if (alertCount > 0) {
            System.out.println("\n🔔 You have " + alertCount + " unread alert(s)!");
        }
        
        pressEnterToContinue();
    }

    private void logSteps() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("           LOG STEPS");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Date (YYYY-MM-DD, or press Enter for today): ");
        LocalDate date = readDate(LocalDate.now());
        
        System.out.print("Number of steps: ");
        int steps = readInt();
        
        if (steps > 0) {
            StepsLog log = individualService.logSteps(currentUser.getId(), steps, date);
            System.out.println("\n✓ Steps logged successfully!");
            System.out.println("   Date: " + log.getLogDate());
            System.out.println("   Steps: " + String.format("%,d", log.getSteps()));
            
            // Show motivation
            if (steps >= 10000) {
                System.out.println("\n🎉 Amazing! You've hit the 10,000 step goal!");
            } else if (steps >= 7500) {
                System.out.println("\n👏 Great job! You're almost at 10,000 steps!");
            } else {
                System.out.println("\n💪 Keep moving! Every step counts!");
            }
        } else {
            System.out.println("\n✗ Invalid number of steps.");
        }
        
        pressEnterToContinue();
    }

    private void logCalories() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          LOG CALORIES");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Date (YYYY-MM-DD, or press Enter for today): ");
        LocalDate date = readDate(LocalDate.now());
        
        System.out.print("Calories consumed: ");
        int consumed = readInt();
        
        System.out.print("Calories burned: ");
        int burned = readInt();
        
        if (consumed >= 0 && burned >= 0) {
            CaloriesLog log = individualService.logCalories(currentUser.getId(), consumed, burned, date);
            System.out.println("\n✓ Calories logged successfully!");
            System.out.println("   Date: " + log.getLogDate());
            System.out.println("   Consumed: " + String.format("%,d", log.getCaloriesConsumed()) + " cal");
            System.out.println("   Burned: " + String.format("%,d", log.getCaloriesBurned()) + " cal");
            System.out.println("   Net: " + String.format("%,d", log.getNetCalories()) + " cal");
        } else {
            System.out.println("\n✗ Invalid calorie values.");
        }
        
        pressEnterToContinue();
    }

    private void logWorkout() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          LOG WORKOUT");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Date (YYYY-MM-DD, or press Enter for today): ");
        LocalDate date = readDate(LocalDate.now());
        
        System.out.println("\nWorkout types: Running, Walking, Cycling, Swimming, Weight Training, Yoga, HIIT, Other");
        System.out.print("Workout type: ");
        String type = scanner.nextLine().trim();
        
        System.out.print("Duration (minutes): ");
        int duration = readInt();
        
        System.out.println("\nIntensity: 1=Low, 2=Medium, 3=High");
        System.out.print("Select intensity: ");
        int intensityChoice = readInt();
        Intensity intensity = switch (intensityChoice) {
            case 1 -> Intensity.LOW;
            case 3 -> Intensity.HIGH;
            default -> Intensity.MEDIUM;
        };
        
        System.out.print("Calories burned (optional, 0 to skip): ");
        int caloriesBurned = readInt();
        
        System.out.print("Notes (optional, press Enter to skip): ");
        String notes = scanner.nextLine().trim();
        
        if (duration > 0 && !type.isEmpty()) {
            Workout workout = individualService.logWorkout(
                currentUser.getId(), type, duration, 
                caloriesBurned > 0 ? caloriesBurned : null,
                intensity, notes.isEmpty() ? null : notes, date
            );
            System.out.println("\n✓ Workout logged successfully!");
            System.out.println("   Type: " + workout.getWorkoutType());
            System.out.println("   Duration: " + workout.getDurationMinutes() + " min");
            System.out.println("   Intensity: " + workout.getIntensity());
            System.out.println("\n💪 Great job completing your workout!");
        } else {
            System.out.println("\n✗ Invalid workout data.");
        }
        
        pressEnterToContinue();
    }

    private void viewProgressHistory() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        PROGRESS HISTORY");
        System.out.println("═══════════════════════════════════════");
        System.out.println("1. Last 7 days");
        System.out.println("2. Last 30 days");
        System.out.println("3. Custom date range");
        System.out.print("\nSelect option: ");
        
        int choice = readInt();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        
        switch (choice) {
            case 1 -> startDate = endDate.minusDays(6);
            case 2 -> startDate = endDate.minusDays(29);
            case 3 -> {
                System.out.print("Start date (YYYY-MM-DD): ");
                startDate = readDate(endDate.minusDays(6));
                System.out.print("End date (YYYY-MM-DD): ");
                endDate = readDate(LocalDate.now());
            }
            default -> startDate = endDate.minusDays(6);
        }
        
        // Steps history
        System.out.println("\n📊 STEPS HISTORY (" + startDate + " to " + endDate + "):");
        List<StepsLog> stepsLogs = individualService.getStepsLogs(currentUser.getId(), startDate, endDate);
        int totalSteps = individualService.getTotalSteps(currentUser.getId(), startDate, endDate);
        double avgSteps = individualService.getAverageSteps(currentUser.getId(), startDate, endDate);
        
        System.out.println("   Total: " + String.format("%,d", totalSteps) + " steps");
        System.out.println("   Average: " + String.format("%,.0f", avgSteps) + " steps/day");
        if (!stepsLogs.isEmpty()) {
            System.out.println("\n   Daily breakdown:");
            for (StepsLog log : stepsLogs) {
                String bar = getProgressBar((double) log.getSteps() / 10000 * 100);
                System.out.println("   " + log.getLogDate() + ": " + bar + " " + String.format("%,d", log.getSteps()));
            }
        }
        
        // Workout history
        System.out.println("\n🏋️ WORKOUT HISTORY:");
        List<Workout> workouts = individualService.getWorkouts(currentUser.getId(), startDate, endDate);
        int totalDuration = individualService.getTotalWorkoutDuration(currentUser.getId(), startDate, endDate);
        int workoutCount = individualService.getWorkoutCount(currentUser.getId(), startDate, endDate);
        
        System.out.println("   Sessions: " + workoutCount);
        System.out.println("   Total duration: " + totalDuration + " minutes");
        if (!workouts.isEmpty()) {
            System.out.println("\n   Recent workouts:");
            for (Workout w : workouts.subList(0, Math.min(5, workouts.size()))) {
                System.out.println("   " + w.getWorkoutDate() + ": " + w.getWorkoutType() + 
                    " (" + w.getDurationMinutes() + " min, " + w.getIntensity() + ")");
            }
        }
        
        pressEnterToContinue();
    }

    private void updateHealthMetrics() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("      UPDATE HEALTH METRICS");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Height (cm): ");
        double height = readDouble();
        
        System.out.print("Weight (kg): ");
        double weight = readDouble();
        
        if (height > 0 && weight > 0) {
            HealthMetrics metrics = individualService.updateHealthMetrics(currentUser.getId(), height, weight);
            System.out.println("\n✓ Health metrics updated successfully!");
            System.out.println("\n📊 YOUR HEALTH STATUS:");
            System.out.println("   Height: " + metrics.getHeightCm() + " cm");
            System.out.println("   Weight: " + metrics.getWeightKg() + " kg");
            System.out.println("   BMI: " + String.format("%.1f", metrics.getBmi()));
            System.out.println("   Classification: " + metrics.getHealthClassification());
            
            // Show recommendation
            System.out.println("\n💡 " + getHealthRecommendation(metrics.getBmi()));
        } else {
            System.out.println("\n✗ Invalid values.");
        }
        
        pressEnterToContinue();
    }

    private void viewGoals() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          YOUR GOALS");
        System.out.println("═══════════════════════════════════════");
        
        List<Goal> activeGoals = individualService.getActiveGoals(currentUser.getId());
        List<Goal> allGoals = individualService.getAllGoals(currentUser.getId());
        
        if (activeGoals.isEmpty()) {
            System.out.println("\nNo active goals. Ask your trainer to set goals for you!");
        } else {
            System.out.println("\n🎯 ACTIVE GOALS:");
            for (Goal goal : activeGoals) {
                System.out.println("\n   " + goal.getGoalType());
                System.out.println("   Target: " + String.format("%.0f", goal.getTargetValue()));
                System.out.println("   Current: " + String.format("%.0f", goal.getCurrentValue()));
                String bar = getProgressBar(goal.getProgressPercentage());
                System.out.println("   Progress: " + bar + " " + String.format("%.1f%%", goal.getProgressPercentage()));
                System.out.println("   Deadline: " + goal.getEndDate());
            }
        }
        
        // Show completed goals
        long completedCount = allGoals.stream().filter(g -> g.getStatus() == GoalStatus.COMPLETED).count();
        if (completedCount > 0) {
            System.out.println("\n✅ Completed goals: " + completedCount);
        }
        
        pressEnterToContinue();
    }

    private void viewAlerts() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          YOUR ALERTS");
        System.out.println("═══════════════════════════════════════");
        
        List<Alert> alerts = individualService.getUnreadAlerts(currentUser.getId());
        
        if (alerts.isEmpty()) {
            System.out.println("\nNo unread alerts. You're all caught up! ✓");
        } else {
            System.out.println("\n🔔 UNREAD ALERTS (" + alerts.size() + "):");
            for (Alert alert : alerts) {
                System.out.println("\n   [" + alert.getAlertType() + "] " + alert.getCreatedAt().toLocalDate());
                System.out.println("   " + alert.getMessage());
            }
            
            System.out.print("\nMark all as read? (y/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
                individualService.markAllAlertsAsRead(currentUser.getId());
                System.out.println("✓ All alerts marked as read.");
            }
        }
        
        pressEnterToContinue();
    }

    private void viewTrainerPlans() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("       TRAINER PLANS FOR YOU");
        System.out.println("═══════════════════════════════════════");
        
        List<TrainerPlan> plans = individualService.getTrainerPlans(currentUser.getId());
        
        if (plans.isEmpty()) {
            System.out.println("\nNo plans from your trainer yet.");
        } else {
            for (TrainerPlan plan : plans) {
                System.out.println("\n╔════════════════════════════════════════");
                System.out.println("║ " + plan.getPlanType() + ": " + plan.getTitle());
                System.out.println("║ Created: " + plan.getCreatedAt().toLocalDate());
                System.out.println("╠════════════════════════════════════════");
                System.out.println(plan.getDescription());
                System.out.println("╚════════════════════════════════════════");
            }
        }
        
        pressEnterToContinue();
    }

    // ==================== Trainer Functions ====================

    private void viewTrainerDashboard() throws SQLException {
        Map<String, Object> dashboard = dashboardService.getTrainerDashboard(currentUser.getId());
        
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    TRAINER DASHBOARD                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n📊 OVERVIEW:");
        System.out.println("   👥 Total Clients: " + dashboard.get("totalClients"));
        System.out.println("   🎯 Total Active Goals: " + dashboard.get("totalActiveGoals"));
        System.out.println("   📝 Total Plans Created: " + dashboard.get("totalPlans"));
        
        // BMI categories
        @SuppressWarnings("unchecked")
        Map<String, Integer> bmiCategories = (Map<String, Integer>) dashboard.get("bmiCategories");
        if (bmiCategories != null && !bmiCategories.isEmpty()) {
            System.out.println("\n⚖️ CLIENTS BY BMI CATEGORY:");
            for (Map.Entry<String, Integer> entry : bmiCategories.entrySet()) {
                System.out.println("   " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        // Clients needing attention
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> needAttention = (List<Map<String, Object>>) dashboard.get("clientsNeedingAttention");
        if (needAttention != null && !needAttention.isEmpty()) {
            System.out.println("\n⚠️ CLIENTS NEEDING ATTENTION:");
            for (Map<String, Object> client : needAttention) {
                User user = (User) client.get("client");
                System.out.println("   • " + user.getFullName() + " - Weekly steps: " + client.get("weeklySteps") + 
                    ", Workouts: " + client.get("weeklyWorkouts"));
            }
        }
        
        pressEnterToContinue();
    }

    private void viewClients() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          YOUR CLIENTS");
        System.out.println("═══════════════════════════════════════");
        
        List<User> clients = trainerService.getClients(currentUser.getId());
        
        if (clients.isEmpty()) {
            System.out.println("\nNo clients assigned yet. Use 'Assign New Client' to add clients.");
        } else {
            System.out.println("\n👥 CLIENT LIST (" + clients.size() + "):");
            for (int i = 0; i < clients.size(); i++) {
                User client = clients.get(i);
                System.out.println("\n   " + (i + 1) + ". " + client.getFullName());
                System.out.println("      Email: " + client.getEmail());
                if (client.getAge() != null) {
                    System.out.println("      Age: " + client.getAge());
                }
            }
        }
        
        pressEnterToContinue();
    }

    private void viewClientProgress() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("    PROGRESS: " + client.getFullName());
        System.out.println("═══════════════════════════════════════");
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        // Steps progress
        Map<String, Object> stepsProgress = trainerService.getClientStepsProgress(client.getId(), startDate, endDate);
        System.out.println("\n👟 STEPS (Last 7 days):");
        System.out.println("   Total: " + String.format("%,d", stepsProgress.get("totalSteps")));
        System.out.println("   Average: " + String.format("%,.0f", stepsProgress.get("averageSteps")) + "/day");
        
        // Workout progress
        Map<String, Object> workoutProgress = trainerService.getClientWorkoutProgress(client.getId(), startDate, endDate);
        System.out.println("\n🏋️ WORKOUTS (Last 7 days):");
        System.out.println("   Sessions: " + workoutProgress.get("workoutCount"));
        System.out.println("   Total Duration: " + workoutProgress.get("totalDuration") + " min");
        System.out.println("   Calories Burned: " + String.format("%,d", workoutProgress.get("totalCaloriesBurned")));
        
        // Health metrics
        Map<String, Object> healthMetrics = trainerService.getClientHealthMetrics(client.getId());
        HealthMetrics latest = (HealthMetrics) healthMetrics.get("latestMetrics");
        if (latest != null) {
            System.out.println("\n⚖️ HEALTH STATUS:");
            System.out.println("   BMI: " + String.format("%.1f", latest.getBmi()) + " (" + latest.getHealthClassification() + ")");
            System.out.println("   Weight: " + latest.getWeightKg() + " kg");
        }
        
        // Goals
        List<Goal> goals = trainerService.getClientGoals(client.getId());
        long activeGoals = goals.stream().filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS).count();
        long completedGoals = goals.stream().filter(g -> g.getStatus() == GoalStatus.COMPLETED).count();
        System.out.println("\n🎯 GOALS:");
        System.out.println("   Active: " + activeGoals);
        System.out.println("   Completed: " + completedGoals);
        
        pressEnterToContinue();
    }

    private void analyzeClientTrends() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.print("Number of weeks to analyze (1-12): ");
        int weeks = Math.min(12, Math.max(1, readInt()));
        
        Map<String, Object> trends = trainerService.analyzeTrends(client.getId(), weeks);
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("   TREND ANALYSIS: " + client.getFullName());
        System.out.println("   Period: Last " + weeks + " weeks");
        System.out.println("═══════════════════════════════════════");
        
        // Weekly data
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> weeklyData = (List<Map<String, Object>>) trends.get("weeklyData");
        
        System.out.println("\n📊 WEEKLY BREAKDOWN:");
        System.out.println(String.format("%-12s %10s %10s %10s", "Week", "Steps", "Workouts", "Duration"));
        System.out.println("─".repeat(50));
        
        for (Map<String, Object> week : weeklyData) {
            LocalDate weekStart = (LocalDate) week.get("weekStart");
            System.out.println(String.format("%-12s %,10d %10d %10d min",
                weekStart.format(DATE_FORMAT),
                week.get("totalSteps"),
                week.get("workoutCount"),
                week.get("totalWorkoutDuration")));
        }
        
        // Trends
        String stepsTrend = (String) trends.getOrDefault("stepsTrend", "N/A");
        String workoutTrend = (String) trends.getOrDefault("workoutTrend", "N/A");
        
        System.out.println("\n📈 TREND INDICATORS:");
        System.out.println("   Steps: " + stepsTrend + " " + getTrendEmoji(stepsTrend));
        System.out.println("   Workouts: " + workoutTrend + " " + getTrendEmoji(workoutTrend));
        
        pressEnterToContinue();
    }

    private void createWorkoutPlan() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("     CREATE WORKOUT PLAN");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Plan title: ");
        String title = scanner.nextLine().trim();
        
        System.out.println("Plan description (end with empty line):");
        StringBuilder description = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            description.append(line).append("\n");
        }
        
        if (!title.isEmpty() && description.length() > 0) {
            TrainerPlan plan = trainerService.createWorkoutPlan(
                currentUser.getId(), client.getId(), title, description.toString().trim());
            System.out.println("\n✓ Workout plan created successfully!");
            System.out.println("   Client: " + client.getFullName());
            System.out.println("   Title: " + plan.getTitle());
        } else {
            System.out.println("\n✗ Invalid plan data.");
        }
        
        pressEnterToContinue();
    }

    private void createDietPlan() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("       CREATE DIET PLAN");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Plan title: ");
        String title = scanner.nextLine().trim();
        
        System.out.println("Plan description (end with empty line):");
        StringBuilder description = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            description.append(line).append("\n");
        }
        
        if (!title.isEmpty() && description.length() > 0) {
            TrainerPlan plan = trainerService.createDietPlan(
                currentUser.getId(), client.getId(), title, description.toString().trim());
            System.out.println("\n✓ Diet plan created successfully!");
            System.out.println("   Client: " + client.getFullName());
            System.out.println("   Title: " + plan.getTitle());
        } else {
            System.out.println("\n✗ Invalid plan data.");
        }
        
        pressEnterToContinue();
    }

    private void createSuggestion() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("       CREATE SUGGESTION");
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("Suggestion title: ");
        String title = scanner.nextLine().trim();
        
        System.out.println("Suggestion details (end with empty line):");
        StringBuilder description = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).isEmpty()) {
            description.append(line).append("\n");
        }
        
        if (!title.isEmpty() && description.length() > 0) {
            TrainerPlan plan = trainerService.createGeneralSuggestion(
                currentUser.getId(), client.getId(), title, description.toString().trim());
            System.out.println("\n✓ Suggestion created successfully!");
            System.out.println("   Client: " + client.getFullName());
            System.out.println("   Title: " + plan.getTitle());
        } else {
            System.out.println("\n✗ Invalid data.");
        }
        
        pressEnterToContinue();
    }

    private void setGoalForClient() throws SQLException {
        List<User> clients = trainerService.getClients(currentUser.getId());
        User client = selectClient(clients);
        if (client == null) return;
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("      SET GOAL FOR CLIENT");
        System.out.println("═══════════════════════════════════════");
        
        System.out.println("\nGoal types:");
        System.out.println("1. Steps");
        System.out.println("2. Calories Burn");
        System.out.println("3. Calories Intake");
        System.out.println("4. Workout Duration");
        System.out.println("5. Weight");
        System.out.print("Select goal type: ");
        
        int typeChoice = readInt();
        GoalType goalType = switch (typeChoice) {
            case 1 -> GoalType.STEPS;
            case 2 -> GoalType.CALORIES_BURN;
            case 3 -> GoalType.CALORIES_INTAKE;
            case 4 -> GoalType.WORKOUT_DURATION;
            case 5 -> GoalType.WEIGHT;
            default -> null;
        };
        
        if (goalType == null) {
            System.out.println("\n✗ Invalid goal type.");
            return;
        }
        
        System.out.print("Target value: ");
        double targetValue = readDouble();
        
        System.out.print("Start date (YYYY-MM-DD, or Enter for today): ");
        LocalDate startDate = readDate(LocalDate.now());
        
        System.out.print("End date (YYYY-MM-DD): ");
        LocalDate endDate = readDate(startDate.plusDays(30));
        
        if (targetValue > 0) {
            Goal goal = trainerService.createGoalForClient(
                currentUser.getId(), client.getId(), goalType, targetValue, startDate, endDate);
            System.out.println("\n✓ Goal created successfully!");
            System.out.println("   Client: " + client.getFullName());
            System.out.println("   Type: " + goal.getGoalType());
            System.out.println("   Target: " + String.format("%.0f", goal.getTargetValue()));
            System.out.println("   Period: " + startDate + " to " + endDate);
        } else {
            System.out.println("\n✗ Invalid target value.");
        }
        
        pressEnterToContinue();
    }

    private void assignClient() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("       ASSIGN NEW CLIENT");
        System.out.println("═══════════════════════════════════════");
        
        List<User> individuals = authService.getAllIndividuals();
        List<User> currentClients = trainerService.getClients(currentUser.getId());
        
        // Filter out already assigned clients
        List<User> availableClients = new ArrayList<>();
        for (User individual : individuals) {
            boolean isAssigned = currentClients.stream().anyMatch(c -> c.getId() == individual.getId());
            if (!isAssigned) {
                availableClients.add(individual);
            }
        }
        
        if (availableClients.isEmpty()) {
            System.out.println("\nNo available clients to assign.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\nAvailable clients:");
        for (int i = 0; i < availableClients.size(); i++) {
            User client = availableClients.get(i);
            System.out.println((i + 1) + ". " + client.getFullName() + " (" + client.getEmail() + ")");
        }
        
        System.out.print("\nSelect client number (0 to cancel): ");
        int choice = readInt();
        
        if (choice > 0 && choice <= availableClients.size()) {
            User selectedClient = availableClients.get(choice - 1);
            trainerService.assignClient(currentUser.getId(), selectedClient.getId());
            System.out.println("\n✓ Client " + selectedClient.getFullName() + " assigned successfully!");
        }
        
        pressEnterToContinue();
    }

    private void viewCreatedPlans() throws SQLException {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("       YOUR CREATED PLANS");
        System.out.println("═══════════════════════════════════════");
        
        List<TrainerPlan> plans = trainerService.getCreatedPlans(currentUser.getId());
        
        if (plans.isEmpty()) {
            System.out.println("\nNo plans created yet.");
        } else {
            for (TrainerPlan plan : plans) {
                System.out.println("\n───────────────────────────────────────");
                System.out.println("📝 " + plan.getPlanType() + ": " + plan.getTitle());
                System.out.println("   Client ID: " + plan.getClientId());
                System.out.println("   Created: " + plan.getCreatedAt().toLocalDate());
            }
        }
        
        pressEnterToContinue();
    }

    // ==================== Chatbot ====================

    private void startChatbot() throws SQLException {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║               AI FITNESS CHATBOT                              ║");
        System.out.println("║         Type 'exit' to return to main menu                    ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        
        // Initial greeting
        String greeting = chatbot.chat(currentUser.getId(), "hello");
        System.out.println("\n🤖 " + greeting);
        
        while (true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("\n🤖 Goodbye! Keep working towards your fitness goals! 💪");
                break;
            }
            
            if (!input.isEmpty()) {
                String response = chatbot.chat(currentUser.getId(), input);
                System.out.println("\n🤖 " + response);
            }
        }
    }

    // ==================== Helper Functions ====================

    private void handleUpdateProfile() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        UPDATE PROFILE");
        System.out.println("═══════════════════════════════════════");
        
        try {
            Optional<User> userOpt = individualService.getUserProfile(currentUser.getId());
            if (userOpt.isEmpty()) return;
            
            User user = userOpt.get();
            
            System.out.print("Full Name [" + user.getFullName() + "]: ");
            String fullName = scanner.nextLine().trim();
            if (!fullName.isEmpty()) user.setFullName(fullName);
            
            System.out.print("Age [" + (user.getAge() != null ? user.getAge() : "not set") + "]: ");
            String ageStr = scanner.nextLine().trim();
            if (!ageStr.isEmpty()) user.setAge(Integer.parseInt(ageStr));
            
            System.out.println("Gender (MALE/FEMALE/OTHER) [" + (user.getGender() != null ? user.getGender() : "not set") + "]: ");
            String genderStr = scanner.nextLine().trim().toUpperCase();
            if (!genderStr.isEmpty()) {
                try {
                    user.setGender(Gender.valueOf(genderStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid gender value.");
                }
            }
            
            individualService.updateUserProfile(user);
            currentUser = user;
            System.out.println("\n✓ Profile updated successfully!");
            
        } catch (SQLException e) {
            System.out.println("\n✗ Failed to update profile: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Invalid number format.");
        }
        
        pressEnterToContinue();
    }

    private void logout() {
        currentUser = null;
        System.out.println("\n✓ Logged out successfully. See you soon!");
    }

    private User selectClient(List<User> clients) {
        if (clients.isEmpty()) {
            System.out.println("\nNo clients assigned. Use 'Assign New Client' first.");
            pressEnterToContinue();
            return null;
        }
        
        System.out.println("\nSelect client:");
        for (int i = 0; i < clients.size(); i++) {
            System.out.println((i + 1) + ". " + clients.get(i).getFullName());
        }
        System.out.print("Choice (0 to cancel): ");
        
        int choice = readInt();
        if (choice > 0 && choice <= clients.size()) {
            return clients.get(choice - 1);
        }
        return null;
    }

    private int readInt() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble() {
        try {
            String line = scanner.nextLine().trim();
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private LocalDate readDate(LocalDate defaultDate) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return defaultDate;
        try {
            return LocalDate.parse(line, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return defaultDate;
        }
    }

    private void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private String getProgressBar(double percentage) {
        int filled = (int) (percentage / 10);
        int empty = 10 - filled;
        return "█".repeat(Math.max(0, filled)) + "░".repeat(Math.max(0, empty));
    }

    private String getTrendEmoji(String trend) {
        return switch (trend) {
            case "IMPROVING" -> "📈";
            case "DECLINING" -> "📉";
            default -> "➡️";
        };
    }

    private String getHealthRecommendation(double bmi) {
        if (bmi < 18.5) {
            return "You're underweight. Consider consulting a nutritionist for a healthy weight gain plan.";
        } else if (bmi < 25) {
            return "Great! You're at a healthy weight. Keep maintaining your current lifestyle!";
        } else if (bmi < 30) {
            return "You're slightly overweight. Regular exercise and balanced diet can help.";
        } else {
            return "Please consult a healthcare professional for a personalized weight management plan.";
        }
    }
}
