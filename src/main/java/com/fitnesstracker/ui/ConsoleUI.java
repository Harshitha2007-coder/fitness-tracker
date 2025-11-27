package com.fitnesstracker.ui;

import com.fitnesstracker.model.*;
import com.fitnesstracker.service.*;
import com.fitnesstracker.dao.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console-based user interface for the Fitness Tracker application.
 */
public class ConsoleUI {
    
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final BMIService bmiService;
    private final DashboardService dashboardService;
    private final FitnessChatbot chatbot;
    private final WorkoutDAO workoutDAO;
    private final DailyStepsDAO dailyStepsDAO;
    private final CalorieIntakeDAO calorieIntakeDAO;
    private final HealthAlertDAO healthAlertDAO;
    
    private User currentUser;
    private boolean running;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthenticationService();
        this.bmiService = new BMIService();
        this.dashboardService = new DashboardService();
        this.chatbot = new FitnessChatbot();
        this.workoutDAO = new WorkoutDAO();
        this.dailyStepsDAO = new DailyStepsDAO();
        this.calorieIntakeDAO = new CalorieIntakeDAO();
        this.healthAlertDAO = new HealthAlertDAO();
        this.running = true;
    }

    /**
     * Start the application.
     */
    public void start() {
        while (running) {
            if (currentUser == null) {
                showLoginMenu();
            } else if (currentUser.getRole() == UserRole.TRAINER) {
                showTrainerMenu();
            } else {
                showIndividualMenu();
            }
        }
        System.out.println("\nThank you for using Fitness Tracker. Stay healthy!");
        scanner.close();
    }

    private void showLoginMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│           LOGIN / REGISTER           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. Login                           │");
        System.out.println("│  2. Register as Individual          │");
        System.out.println("│  3. Register as Trainer             │");
        System.out.println("│  4. Exit                            │");
        System.out.println("└─────────────────────────────────────┘");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegistration(UserRole.INDIVIDUAL);
                break;
            case "3":
                handleRegistration(UserRole.TRAINER);
                break;
            case "4":
                running = false;
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private void handleLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        try {
            Optional<User> userOpt = authService.login(username, password);
            if (userOpt.isPresent()) {
                currentUser = userOpt.get();
                System.out.println("\n✓ Welcome back, " + currentUser.getFirstName() + "!");
                checkUnreadAlerts();
            } else {
                System.out.println("\n✗ Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void handleRegistration(UserRole role) {
        System.out.println("\n=== REGISTER AS " + role + " ===");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("✗ Passwords do not match.");
            return;
        }
        
        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();
        
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();
        
        try {
            User user = authService.register(username, email, password, role, firstName, lastName);
            System.out.println("\n✓ Registration successful! Welcome, " + user.getFirstName() + "!");
            currentUser = user;
        } catch (IllegalArgumentException e) {
            System.out.println("✗ Registration failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showIndividualMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│          FITNESS DASHBOARD           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. View Dashboard                  │");
        System.out.println("│  2. Log Steps                       │");
        System.out.println("│  3. Log Workout                     │");
        System.out.println("│  4. Log Meal/Calories               │");
        System.out.println("│  5. Record Health Metrics           │");
        System.out.println("│  6. View Health Assessment          │");
        System.out.println("│  7. View Alerts                     │");
        System.out.println("│  8. Chat with AI Assistant          │");
        System.out.println("│  9. View Progress Report            │");
        System.out.println("│  0. Logout                          │");
        System.out.println("└─────────────────────────────────────┘");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        try {
            switch (choice) {
                case "1":
                    viewDashboard();
                    break;
                case "2":
                    logSteps();
                    break;
                case "3":
                    logWorkout();
                    break;
                case "4":
                    logMeal();
                    break;
                case "5":
                    recordHealthMetrics();
                    break;
                case "6":
                    viewHealthAssessment();
                    break;
                case "7":
                    viewAlerts();
                    break;
                case "8":
                    startChatbot();
                    break;
                case "9":
                    viewProgressReport();
                    break;
                case "0":
                    logout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void showTrainerMenu() {
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│          TRAINER DASHBOARD           │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│  1. View Dashboard                  │");
        System.out.println("│  2. View Client List                │");
        System.out.println("│  3. View Client Progress            │");
        System.out.println("│  4. Analyze Trends                  │");
        System.out.println("│  5. Chat with AI Assistant          │");
        System.out.println("│  0. Logout                          │");
        System.out.println("└─────────────────────────────────────┘");
        System.out.print("Choose an option: ");
        
        String choice = scanner.nextLine().trim();
        
        try {
            switch (choice) {
                case "1":
                    viewTrainerDashboard();
                    break;
                case "2":
                    viewClientList();
                    break;
                case "3":
                    viewClientProgress();
                    break;
                case "4":
                    analyzeTrends();
                    break;
                case "5":
                    startChatbot();
                    break;
                case "0":
                    logout();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void viewDashboard() throws SQLException {
        Map<String, Object> dashboard = dashboardService.getIndividualDashboard(currentUser.getId());
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    YOUR FITNESS DASHBOARD                   ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        System.out.println("║ TODAY'S STATS:                                             ║");
        System.out.printf("║   Steps: %,d                                              \n", dashboard.get("todaySteps"));
        System.out.printf("║   Calories consumed: %,d                                  \n", dashboard.get("todayCalories"));
        
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ THIS WEEK:                                                 ║");
        System.out.printf("║   Total steps: %,d                                        \n", dashboard.get("weeklySteps"));
        System.out.printf("║   Avg daily steps: %.0f                                   \n", dashboard.get("avgDailySteps"));
        System.out.printf("║   Workouts completed: %d                                  \n", dashboard.get("weeklyWorkouts"));
        System.out.printf("║   Workout duration: %d minutes                            \n", dashboard.get("weeklyWorkoutDuration"));
        System.out.printf("║   Calories burned: %,d                                    \n", dashboard.get("weeklyCaloriesBurned"));
        System.out.printf("║   Days goal achieved: %d                                  \n", dashboard.get("daysGoalAchieved"));
        
        if (dashboard.containsKey("currentBMI")) {
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║ HEALTH METRICS:                                            ║");
            System.out.printf("║   Current weight: %.1f kg                                  \n", dashboard.get("currentWeight"));
            System.out.printf("║   BMI: %.1f (%s)                                    \n", dashboard.get("currentBMI"), dashboard.get("bmiCategory"));
            System.out.printf("║   Monthly weight change: %.1f kg                          \n", dashboard.get("monthlyWeightChange"));
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        int alertCount = (int) dashboard.get("alertCount");
        if (alertCount > 0) {
            System.out.println("\n⚠️  You have " + alertCount + " unread alerts!");
        }
    }

    private void logSteps() throws SQLException {
        System.out.println("\n=== LOG STEPS ===");
        System.out.print("Enter step count: ");
        
        try {
            int stepCount = Integer.parseInt(scanner.nextLine().trim());
            
            if (stepCount < 0 || stepCount > 100000) {
                System.out.println("✗ Invalid step count. Please enter a value between 0 and 100,000.");
                return;
            }
            
            System.out.print("Enter step goal (default 10000): ");
            String goalInput = scanner.nextLine().trim();
            int goalSteps = goalInput.isEmpty() ? 10000 : Integer.parseInt(goalInput);
            
            DailySteps steps = new DailySteps(currentUser.getId(), stepCount, LocalDate.now());
            steps.setGoalSteps(goalSteps);
            dailyStepsDAO.createOrUpdate(steps);
            
            System.out.println("\n✓ Steps logged successfully!");
            System.out.printf("Progress: %.1f%% of goal\n", steps.getGoalProgress());
            
            if (steps.isGoalAchieved()) {
                System.out.println("🎉 Congratulations! You've reached your step goal!");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid input. Please enter a valid number.");
        }
    }

    private void logWorkout() throws SQLException {
        System.out.println("\n=== LOG WORKOUT ===");
        
        System.out.println("Workout Types: Running, Walking, Cycling, Swimming, Strength Training, Yoga, HIIT, Other");
        System.out.print("Workout type: ");
        String type = scanner.nextLine().trim();
        
        System.out.print("Duration (minutes): ");
        int duration;
        try {
            duration = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid duration.");
            return;
        }
        
        System.out.print("Calories burned (or press Enter to skip): ");
        String caloriesInput = scanner.nextLine().trim();
        int calories = caloriesInput.isEmpty() ? estimateCalories(type, duration) : Integer.parseInt(caloriesInput);
        
        System.out.println("Intensity (LOW, MEDIUM, HIGH): ");
        String intensity = scanner.nextLine().trim().toUpperCase();
        if (!intensity.equals("LOW") && !intensity.equals("MEDIUM") && !intensity.equals("HIGH")) {
            intensity = "MEDIUM";
        }
        
        System.out.print("Notes (optional): ");
        String notes = scanner.nextLine().trim();
        
        Workout workout = new Workout(currentUser.getId(), type, duration, calories, intensity, LocalDate.now());
        workout.setNotes(notes.isEmpty() ? null : notes);
        workoutDAO.create(workout);
        
        System.out.println("\n✓ Workout logged successfully!");
        System.out.println("   Type: " + type);
        System.out.println("   Duration: " + duration + " minutes");
        System.out.println("   Calories burned: " + calories);
    }

    private int estimateCalories(String workoutType, int duration) {
        // Rough calorie estimates per minute
        double caloriesPerMinute;
        switch (workoutType.toLowerCase()) {
            case "running":
                caloriesPerMinute = 10;
                break;
            case "cycling":
                caloriesPerMinute = 8;
                break;
            case "swimming":
                caloriesPerMinute = 9;
                break;
            case "hiit":
                caloriesPerMinute = 12;
                break;
            case "strength training":
                caloriesPerMinute = 6;
                break;
            case "yoga":
                caloriesPerMinute = 3;
                break;
            case "walking":
                caloriesPerMinute = 4;
                break;
            default:
                caloriesPerMinute = 5;
        }
        return (int) (caloriesPerMinute * duration);
    }

    private void logMeal() throws SQLException {
        System.out.println("\n=== LOG MEAL/CALORIES ===");
        
        System.out.println("Meal Types: BREAKFAST, LUNCH, DINNER, SNACK");
        System.out.print("Meal type: ");
        String mealType = scanner.nextLine().trim().toUpperCase();
        
        System.out.print("Food item: ");
        String foodItem = scanner.nextLine().trim();
        
        System.out.print("Calories: ");
        int calories;
        try {
            calories = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid calories.");
            return;
        }
        
        System.out.print("Protein (g) [optional]: ");
        String proteinInput = scanner.nextLine().trim();
        double protein = proteinInput.isEmpty() ? 0 : Double.parseDouble(proteinInput);
        
        System.out.print("Carbs (g) [optional]: ");
        String carbsInput = scanner.nextLine().trim();
        double carbs = carbsInput.isEmpty() ? 0 : Double.parseDouble(carbsInput);
        
        System.out.print("Fat (g) [optional]: ");
        String fatInput = scanner.nextLine().trim();
        double fat = fatInput.isEmpty() ? 0 : Double.parseDouble(fatInput);
        
        CalorieIntake intake = new CalorieIntake(currentUser.getId(), mealType, foodItem, calories, LocalDate.now());
        intake.setProteinG(protein);
        intake.setCarbsG(carbs);
        intake.setFatG(fat);
        calorieIntakeDAO.create(intake);
        
        System.out.println("\n✓ Meal logged successfully!");
        
        int todayTotal = calorieIntakeDAO.getTotalCaloriesForDate(currentUser.getId(), LocalDate.now());
        System.out.println("Today's total calories: " + todayTotal);
    }

    private void recordHealthMetrics() throws SQLException {
        System.out.println("\n=== RECORD HEALTH METRICS ===");
        
        System.out.print("Weight (kg): ");
        double weight;
        try {
            weight = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid weight.");
            return;
        }
        
        System.out.print("Height (cm): ");
        double height;
        try {
            height = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid height.");
            return;
        }
        
        System.out.print("Blood pressure systolic (optional, press Enter to skip): ");
        String bpSysInput = scanner.nextLine().trim();
        Integer bpSys = bpSysInput.isEmpty() ? null : Integer.parseInt(bpSysInput);
        
        System.out.print("Blood pressure diastolic (optional): ");
        String bpDiaInput = scanner.nextLine().trim();
        Integer bpDia = bpDiaInput.isEmpty() ? null : Integer.parseInt(bpDiaInput);
        
        System.out.print("Resting heart rate (optional): ");
        String hrInput = scanner.nextLine().trim();
        Integer hr = hrInput.isEmpty() ? null : Integer.parseInt(hrInput);
        
        HealthMetrics metrics = bmiService.recordHealthMetrics(
            currentUser.getId(), weight, height, bpSys, bpDia, hr);
        
        System.out.println("\n✓ Health metrics recorded!");
        System.out.printf("BMI: %.1f\n", metrics.getBmi());
        System.out.println("Category: " + metrics.getBmiCategory().getDisplayName());
        System.out.println("\n" + metrics.getBmiCategory().getHealthAdvice());
    }

    private void viewHealthAssessment() throws SQLException {
        System.out.println("\n=== HEALTH ASSESSMENT ===");
        String assessment = bmiService.getHealthAssessment(currentUser.getId());
        System.out.println(assessment);
        
        System.out.println("\n" + bmiService.getPersonalizedGoals(currentUser));
    }

    private void viewAlerts() throws SQLException {
        System.out.println("\n=== HEALTH ALERTS ===");
        
        List<HealthAlert> alerts = healthAlertDAO.findUnreadByUserId(currentUser.getId());
        
        if (alerts.isEmpty()) {
            System.out.println("No unread alerts.");
            return;
        }
        
        for (HealthAlert alert : alerts) {
            System.out.println("\n" + alert);
            System.out.println("  Created: " + alert.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        
        System.out.print("\nMark all as read? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            healthAlertDAO.markAllAsRead(currentUser.getId());
            System.out.println("✓ All alerts marked as read.");
        }
    }

    private void startChatbot() throws SQLException {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║              AI FITNESS ASSISTANT                           ║");
        System.out.println("║  Type 'exit' or 'quit' to return to main menu              ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        System.out.println("\n" + chatbot.chat(currentUser.getId(), "hello"));
        
        while (true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("\nGoodbye! Stay healthy! 💪");
                break;
            }
            
            if (input.isEmpty()) {
                continue;
            }
            
            String response = chatbot.chat(currentUser.getId(), input);
            System.out.println("\n🤖 Assistant: " + response);
        }
    }

    private void viewProgressReport() throws SQLException {
        System.out.println("\n=== PROGRESS REPORT ===");
        System.out.println("Select time period:");
        System.out.println("1. Last 7 days");
        System.out.println("2. Last 30 days");
        System.out.println("3. Custom range");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine().trim();
        LocalDate startDate;
        LocalDate endDate = LocalDate.now();
        
        switch (choice) {
            case "1":
                startDate = endDate.minusDays(7);
                break;
            case "2":
                startDate = endDate.minusDays(30);
                break;
            case "3":
                System.out.print("Start date (YYYY-MM-DD): ");
                try {
                    startDate = LocalDate.parse(scanner.nextLine().trim());
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                    return;
                }
                System.out.print("End date (YYYY-MM-DD): ");
                try {
                    endDate = LocalDate.parse(scanner.nextLine().trim());
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format.");
                    return;
                }
                break;
            default:
                startDate = endDate.minusDays(7);
        }
        
        Map<String, Object> report = dashboardService.getProgressReport(currentUser.getId(), startDate, endDate);
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    PROGRESS REPORT                          ║");
        System.out.printf("║            %s to %s                      \n", startDate, endDate);
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        
        System.out.println("║ STEPS:                                                     ║");
        System.out.printf("║   Total: %,d                                              \n", report.get("totalSteps"));
        System.out.printf("║   Daily average: %.0f                                     \n", report.get("avgDailySteps"));
        System.out.printf("║   Days goal met: %d                                       \n", report.get("daysStepGoalMet"));
        
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ WORKOUTS:                                                  ║");
        System.out.printf("║   Total sessions: %d                                      \n", report.get("totalWorkoutCount"));
        System.out.printf("║   Total duration: %d minutes                              \n", report.get("totalWorkoutDuration"));
        System.out.printf("║   Calories burned: %,d                                    \n", report.get("totalCaloriesBurned"));
        
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ NUTRITION:                                                 ║");
        System.out.printf("║   Total calories consumed: %,d                            \n", report.get("totalCaloriesConsumed"));
        System.out.printf("║   Daily average: %.0f                                     \n", report.get("avgDailyCalories"));
        System.out.printf("║   Net calories: %,d                                       \n", report.get("netCalories"));
        
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║ WEIGHT:                                                    ║");
        System.out.printf("║   Change: %.1f kg                                         \n", report.get("weightChange"));
        
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }

    private void viewTrainerDashboard() throws SQLException {
        Map<String, Object> dashboard = dashboardService.getTrainerDashboard(currentUser.getId());
        
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                   TRAINER DASHBOARD                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Total Clients: %d                                          \n", dashboard.get("totalClients"));
        System.out.printf("║ Clients Needing Attention: %d                              \n", dashboard.get("clientsNeedingAttention"));
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        @SuppressWarnings("unchecked")
        Map<Integer, Map<String, Object>> summaries = 
            (Map<Integer, Map<String, Object>>) dashboard.get("clientSummaries");
        
        if (!summaries.isEmpty()) {
            System.out.println("\nClient Overview:");
            for (Map.Entry<Integer, Map<String, Object>> entry : summaries.entrySet()) {
                Map<String, Object> summary = entry.getValue();
                System.out.println("─────────────────────────────────────");
                System.out.println("Name: " + summary.get("name"));
                System.out.println("  Weekly workouts: " + summary.get("weeklyWorkouts"));
                System.out.printf("  Avg daily steps: %.0f\n", summary.get("avgDailySteps"));
                if (summary.containsKey("currentBMI")) {
                    System.out.printf("  BMI: %.1f (%s)\n", summary.get("currentBMI"), summary.get("bmiCategory"));
                    if ((boolean) summary.get("needsAttention")) {
                        System.out.println("  ⚠️  Needs attention");
                    }
                }
            }
        }
    }

    private void viewClientList() throws SQLException {
        UserDAO userDAO = new UserDAO();
        List<User> clients = userDAO.findIndividualsByTrainer(currentUser.getId());
        
        System.out.println("\n=== YOUR CLIENTS ===");
        
        if (clients.isEmpty()) {
            System.out.println("No clients assigned yet.");
            return;
        }
        
        int index = 1;
        for (User client : clients) {
            System.out.println(index + ". " + client.getFullName() + " (@" + client.getUsername() + ")");
            index++;
        }
    }

    private void viewClientProgress() throws SQLException {
        UserDAO userDAO = new UserDAO();
        List<User> clients = userDAO.findIndividualsByTrainer(currentUser.getId());
        
        if (clients.isEmpty()) {
            System.out.println("No clients assigned yet.");
            return;
        }
        
        System.out.println("\n=== VIEW CLIENT PROGRESS ===");
        viewClientList();
        
        System.out.print("\nSelect client number: ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            if (selection < 1 || selection > clients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            User client = clients.get(selection - 1);
            LocalDate startDate = LocalDate.now().minusDays(30);
            Map<String, Object> report = dashboardService.getProgressReport(client.getId(), startDate, LocalDate.now());
            
            System.out.println("\n=== Progress Report for " + client.getFullName() + " ===");
            System.out.println("(Last 30 days)");
            System.out.println("\nSteps: " + report.get("totalSteps") + " total, " + 
                             String.format("%.0f", report.get("avgDailySteps")) + " daily avg");
            System.out.println("Workouts: " + report.get("totalWorkoutCount") + " sessions, " +
                             report.get("totalWorkoutDuration") + " min total");
            System.out.println("Calories: " + report.get("totalCaloriesConsumed") + " consumed, " +
                             report.get("totalCaloriesBurned") + " burned");
            System.out.println("Weight change: " + String.format("%.1f", report.get("weightChange")) + " kg");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void analyzeTrends() throws SQLException {
        UserDAO userDAO = new UserDAO();
        List<User> clients = userDAO.findIndividualsByTrainer(currentUser.getId());
        
        if (clients.isEmpty()) {
            System.out.println("No clients assigned yet.");
            return;
        }
        
        System.out.println("\n=== TREND ANALYSIS ===");
        viewClientList();
        
        System.out.print("\nSelect client number: ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            if (selection < 1 || selection > clients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            User client = clients.get(selection - 1);
            Map<String, List<?>> trends = dashboardService.getTrendData(client.getId(), 30);
            
            System.out.println("\n=== 30-Day Trends for " + client.getFullName() + " ===");
            
            @SuppressWarnings("unchecked")
            List<DailySteps> stepsTrend = (List<DailySteps>) trends.get("steps");
            System.out.println("\nStep Records: " + stepsTrend.size() + " days logged");
            
            @SuppressWarnings("unchecked")
            List<Workout> workoutTrend = (List<Workout>) trends.get("workouts");
            System.out.println("Workout Records: " + workoutTrend.size() + " sessions");
            
            @SuppressWarnings("unchecked")
            List<HealthMetrics> metricsTrend = (List<HealthMetrics>) trends.get("healthMetrics");
            if (!metricsTrend.isEmpty()) {
                System.out.println("\nWeight Trend:");
                for (HealthMetrics m : metricsTrend) {
                    System.out.printf("  %s: %.1f kg (BMI: %.1f)\n", 
                                    m.getMeasurementDate(), m.getWeightKg(), m.getBmi());
                }
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    private void checkUnreadAlerts() throws SQLException {
        int alertCount = healthAlertDAO.getUnreadCount(currentUser.getId());
        if (alertCount > 0) {
            System.out.println("\n⚠️  You have " + alertCount + " unread health alert(s)!");
        }
    }

    private void logout() {
        System.out.println("\nGoodbye, " + currentUser.getFirstName() + "!");
        currentUser = null;
    }
}
