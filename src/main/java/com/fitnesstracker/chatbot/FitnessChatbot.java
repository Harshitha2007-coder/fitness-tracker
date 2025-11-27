package com.fitnesstracker.chatbot;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;
import com.fitnesstracker.util.BMICalculator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

/**
 * AI Chatbot for providing fitness tips, workout suggestions, diet advice,
 * and encouraging healthy habits through interactive dialogue.
 */
public class FitnessChatbot {
    private final UserDAO userDAO;
    private final StepsLogDAO stepsLogDAO;
    private final CaloriesLogDAO caloriesLogDAO;
    private final WorkoutDAO workoutDAO;
    private final HealthMetricsDAO healthMetricsDAO;
    private final GoalDAO goalDAO;
    private final ChatMessageDAO chatMessageDAO;

    private static final Map<String, List<String>> WORKOUT_SUGGESTIONS = new HashMap<>();
    private static final Map<String, List<String>> DIET_TIPS = new HashMap<>();
    private static final Map<String, List<String>> MOTIVATION_QUOTES = new HashMap<>();
    private static final List<String> GENERAL_FITNESS_TIPS = new ArrayList<>();

    static {
        initializeKnowledgeBase();
    }

    public FitnessChatbot() {
        this.userDAO = new UserDAO();
        this.stepsLogDAO = new StepsLogDAO();
        this.caloriesLogDAO = new CaloriesLogDAO();
        this.workoutDAO = new WorkoutDAO();
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.goalDAO = new GoalDAO();
        this.chatMessageDAO = new ChatMessageDAO();
    }

    /**
     * Process a user message and generate a response.
     */
    public String chat(int userId, String message) throws SQLException {
        String lowerMessage = message.toLowerCase().trim();
        String response = generateResponse(userId, lowerMessage, message);
        
        // Save chat history
        chatMessageDAO.create(new ChatMessage(userId, message, response));
        
        return response;
    }

    /**
     * Generate appropriate response based on message content.
     */
    private String generateResponse(int userId, String lowerMessage, String originalMessage) throws SQLException {
        // Greeting patterns
        if (matchesPattern(lowerMessage, "hello", "hi", "hey", "good morning", "good evening", "good afternoon")) {
            return generateGreeting(userId);
        }

        // Help request
        if (matchesPattern(lowerMessage, "help", "what can you do", "commands", "features")) {
            return generateHelpMessage();
        }

        // BMI and health queries
        if (matchesPattern(lowerMessage, "bmi", "body mass index", "weight status", "health status")) {
            return generateBMIResponse(userId);
        }

        // Workout suggestions
        if (matchesPattern(lowerMessage, "workout", "exercise", "training", "fitness routine")) {
            return generateWorkoutSuggestion(userId);
        }

        // Diet and nutrition
        if (matchesPattern(lowerMessage, "diet", "nutrition", "eat", "food", "meal", "healthy eating")) {
            return generateDietAdvice(userId);
        }

        // Steps and activity
        if (matchesPattern(lowerMessage, "steps", "walking", "step count", "activity")) {
            return generateStepsAdvice(userId);
        }

        // Calories
        if (matchesPattern(lowerMessage, "calories", "calorie", "burn", "intake")) {
            return generateCaloriesAdvice(userId);
        }

        // Goals
        if (matchesPattern(lowerMessage, "goal", "target", "objective", "aim")) {
            return generateGoalsResponse(userId);
        }

        // Progress query
        if (matchesPattern(lowerMessage, "progress", "how am i doing", "status", "summary", "stats")) {
            return generateProgressSummary(userId);
        }

        // Motivation
        if (matchesPattern(lowerMessage, "motivation", "motivate", "inspire", "encourage", "feeling lazy", "dont want", "can't continue")) {
            return generateMotivation(userId);
        }

        // Weight loss
        if (matchesPattern(lowerMessage, "lose weight", "weight loss", "slim down", "fat loss")) {
            return generateWeightLossAdvice(userId);
        }

        // Weight gain / muscle building
        if (matchesPattern(lowerMessage, "gain weight", "build muscle", "bulk", "mass", "strength")) {
            return generateMuscleGainAdvice(userId);
        }

        // Sleep and recovery
        if (matchesPattern(lowerMessage, "sleep", "rest", "recovery", "tired", "fatigue")) {
            return generateSleepAdvice();
        }

        // Hydration
        if (matchesPattern(lowerMessage, "water", "hydration", "drink", "fluid")) {
            return generateHydrationAdvice();
        }

        // Specific workout types
        if (matchesPattern(lowerMessage, "cardio", "running", "jogging")) {
            return getWorkoutTypeAdvice("cardio");
        }
        if (matchesPattern(lowerMessage, "yoga", "stretch", "flexibility")) {
            return getWorkoutTypeAdvice("yoga");
        }
        if (matchesPattern(lowerMessage, "weight", "lifting", "strength training", "resistance")) {
            return getWorkoutTypeAdvice("strength");
        }
        if (matchesPattern(lowerMessage, "hiit", "high intensity", "interval")) {
            return getWorkoutTypeAdvice("hiit");
        }

        // Tips
        if (matchesPattern(lowerMessage, "tip", "advice", "suggestion", "recommend")) {
            return generateRandomTip();
        }

        // Thank you
        if (matchesPattern(lowerMessage, "thank", "thanks", "appreciate")) {
            return "You're welcome! 😊 Remember, consistency is key to achieving your fitness goals. Keep up the great work! Is there anything else I can help you with?";
        }

        // Goodbye
        if (matchesPattern(lowerMessage, "bye", "goodbye", "see you", "exit", "quit")) {
            return "Goodbye! 👋 Keep moving and stay healthy! Remember, every step counts. See you next time!";
        }

        // Default response
        return generateDefaultResponse(originalMessage);
    }

    /**
     * Generate personalized greeting.
     */
    private String generateGreeting(int userId) throws SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        String name = userOpt.map(User::getFullName).orElse("friend");
        
        LocalDate today = LocalDate.now();
        Optional<StepsLog> todaySteps = stepsLogDAO.findByUserIdAndDate(userId, today);
        
        StringBuilder greeting = new StringBuilder();
        greeting.append(String.format("Hello, %s! 👋 Welcome to your Fitness Assistant!\n\n", name));
        
        if (todaySteps.isPresent()) {
            int steps = todaySteps.get().getSteps();
            greeting.append(String.format("Great job! You've already logged %,d steps today! ", steps));
            if (steps >= 10000) {
                greeting.append("🎉 You've hit your daily goal!\n\n");
            } else {
                greeting.append(String.format("Keep going! %,d more steps to reach 10,000! 💪\n\n", 10000 - steps));
            }
        } else {
            greeting.append("Ready to crush your fitness goals today? 💪\n\n");
        }
        
        greeting.append("How can I help you today? You can ask me about:\n");
        greeting.append("• Workout suggestions\n");
        greeting.append("• Diet and nutrition tips\n");
        greeting.append("• Your BMI and health status\n");
        greeting.append("• Progress tracking\n");
        greeting.append("• Motivation and tips");
        
        return greeting.toString();
    }

    /**
     * Generate help message.
     */
    private String generateHelpMessage() {
        return """
            🤖 I'm your AI Fitness Assistant! Here's what I can help you with:
            
            📊 **Health & Metrics**
            • "What's my BMI?" - Get your BMI and health classification
            • "My progress" - View your fitness progress summary
            • "My goals" - Check your active goals
            
            🏋️ **Workout Help**
            • "Workout suggestion" - Get personalized workout ideas
            • "Cardio tips" - Cardio exercise recommendations
            • "Strength training" - Weight training advice
            • "Yoga tips" - Flexibility and mindfulness
            
            🥗 **Nutrition**
            • "Diet advice" - Healthy eating tips
            • "Calories" - Calorie management guidance
            • "Hydration" - Water intake recommendations
            
            💪 **Goals & Motivation**
            • "Help me lose weight" - Weight loss strategies
            • "Build muscle" - Muscle gain advice
            • "Motivate me" - Get inspired!
            • "Give me a tip" - Random fitness tip
            
            Just type your question naturally, and I'll do my best to help! 😊
            """;
    }

    /**
     * Generate BMI response.
     */
    private String generateBMIResponse(int userId) throws SQLException {
        Optional<User> userOpt = userDAO.findById(userId);
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(userId);
        
        if (metricsOpt.isEmpty()) {
            return """
                I don't have your BMI data yet! 📊
                
                To calculate your BMI, please update your profile with your height and weight.
                
                BMI (Body Mass Index) is a simple measure that uses your height and weight to determine if your weight is healthy. Here's the scale:
                • Under 18.5: Underweight
                • 18.5 - 24.9: Normal (healthy)
                • 25.0 - 29.9: Overweight
                • 30.0 and above: Obese
                
                Would you like me to provide general health tips while you update your profile?
                """;
        }
        
        HealthMetrics metrics = metricsOpt.get();
        double bmi = metrics.getBmi();
        String classification = metrics.getHealthClassification();
        String risk = BMICalculator.getHealthRisk(bmi);
        
        StringBuilder response = new StringBuilder();
        response.append(String.format("📊 **Your BMI Analysis**\n\n"));
        response.append(String.format("BMI: %.1f\n", bmi));
        response.append(String.format("Classification: %s\n", classification));
        response.append(String.format("Health Risk: %s\n\n", risk));
        
        // Personalized advice based on BMI
        if (bmi < 18.5) {
            response.append("💡 **Recommendations:**\n");
            response.append("• Focus on nutrient-dense foods\n");
            response.append("• Include healthy fats and proteins\n");
            response.append("• Consider strength training to build muscle mass\n");
            response.append("• Consult a nutritionist for a personalized plan\n");
        } else if (bmi < 25) {
            response.append("✅ **Great job maintaining a healthy weight!**\n");
            response.append("💡 **To stay on track:**\n");
            response.append("• Keep up your current activity level\n");
            response.append("• Maintain a balanced diet\n");
            response.append("• Continue monitoring your progress\n");
        } else if (bmi < 30) {
            response.append("💡 **Recommendations:**\n");
            response.append("• Increase physical activity gradually\n");
            response.append("• Focus on portion control\n");
            response.append("• Choose whole foods over processed ones\n");
            response.append("• Aim for 150+ minutes of moderate exercise weekly\n");
        } else {
            response.append("💡 **Recommendations:**\n");
            response.append("• Start with low-impact exercises like walking or swimming\n");
            response.append("• Work with a healthcare provider for a safe weight loss plan\n");
            response.append("• Focus on sustainable lifestyle changes\n");
            response.append("• Small, consistent changes lead to big results!\n");
        }
        
        // Add ideal weight range
        if (userOpt.isPresent() && userOpt.get().getHeightCm() != null) {
            double[] range = BMICalculator.getRecommendedWeightRange(userOpt.get().getHeightCm());
            response.append(String.format("\n📏 Healthy weight range for your height: %.1f - %.1f kg", range[0], range[1]));
        }
        
        return response.toString();
    }

    /**
     * Generate workout suggestion based on user data.
     */
    private String generateWorkoutSuggestion(int userId) throws SQLException {
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(userId);
        List<Workout> recentWorkouts = workoutDAO.findByUserIdAndDateRange(userId, 
            LocalDate.now().minusDays(7), LocalDate.now());
        
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("🏋️ **Personalized Workout Suggestion**\n\n");
        
        // Analyze recent workout patterns
        Map<String, Integer> workoutTypes = new HashMap<>();
        for (Workout w : recentWorkouts) {
            workoutTypes.merge(w.getWorkoutType(), 1, Integer::sum);
        }
        
        if (recentWorkouts.isEmpty()) {
            suggestion.append("Looks like you haven't logged workouts recently. Let's get you started!\n\n");
            suggestion.append("**Beginner-Friendly Weekly Plan:**\n");
            suggestion.append("• Monday: 20-min brisk walk + 10-min stretching\n");
            suggestion.append("• Wednesday: 15-min bodyweight exercises\n");
            suggestion.append("• Friday: 25-min light cardio (walking, cycling)\n");
            suggestion.append("• Weekend: Active recovery (yoga, leisure walk)\n");
        } else {
            int totalMinutes = recentWorkouts.stream().mapToInt(Workout::getDurationMinutes).sum();
            suggestion.append(String.format("This week: %d workouts, %d minutes total 💪\n\n", 
                recentWorkouts.size(), totalMinutes));
            
            // Suggest variety
            if (!workoutTypes.containsKey("Cardio")) {
                suggestion.append("💡 Try adding some cardio for heart health!\n");
            }
            if (!workoutTypes.containsKey("Strength") && !workoutTypes.containsKey("Weight Training")) {
                suggestion.append("💡 Include strength training to build muscle and boost metabolism!\n");
            }
            if (!workoutTypes.containsKey("Yoga") && !workoutTypes.containsKey("Stretching")) {
                suggestion.append("💡 Add flexibility work to prevent injuries!\n");
            }
        }
        
        // Adjust based on BMI
        if (metricsOpt.isPresent()) {
            double bmi = metricsOpt.get().getBmi();
            suggestion.append("\n**Based on your health profile:**\n");
            if (bmi < 18.5) {
                suggestion.append("• Focus on strength training to build muscle mass\n");
                suggestion.append("• Include compound exercises (squats, deadlifts, bench press)\n");
            } else if (bmi >= 25) {
                suggestion.append("• Start with low-impact cardio (swimming, cycling, elliptical)\n");
                suggestion.append("• Gradually increase intensity as fitness improves\n");
            }
        }
        
        // Add a specific workout
        suggestion.append("\n**Today's Suggested Workout:**\n");
        suggestion.append(getRandomWorkoutRoutine());
        
        return suggestion.toString();
    }

    /**
     * Generate diet advice.
     */
    private String generateDietAdvice(int userId) throws SQLException {
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(userId);
        
        StringBuilder advice = new StringBuilder();
        advice.append("🥗 **Nutrition & Diet Tips**\n\n");
        
        // General principles
        advice.append("**Core Principles:**\n");
        advice.append("• Eat a variety of colorful fruits and vegetables\n");
        advice.append("• Choose whole grains over refined grains\n");
        advice.append("• Include lean proteins (chicken, fish, legumes)\n");
        advice.append("• Limit processed foods and added sugars\n");
        advice.append("• Stay hydrated - aim for 8 glasses of water daily\n\n");
        
        // Personalized based on BMI
        if (metricsOpt.isPresent()) {
            double bmi = metricsOpt.get().getBmi();
            advice.append("**Personalized Recommendations:**\n");
            
            if (bmi < 18.5) {
                advice.append("Since you're underweight:\n");
                advice.append("• Eat more frequently (5-6 smaller meals)\n");
                advice.append("• Add healthy calorie-dense foods (nuts, avocados, olive oil)\n");
                advice.append("• Include protein with every meal\n");
                advice.append("• Don't skip breakfast!\n");
            } else if (bmi < 25) {
                advice.append("You're at a healthy weight! To maintain:\n");
                advice.append("• Keep a balanced macronutrient ratio\n");
                advice.append("• Practice mindful eating\n");
                advice.append("• Listen to your hunger cues\n");
            } else {
                advice.append("For healthy weight management:\n");
                advice.append("• Focus on portion control\n");
                advice.append("• Fill half your plate with vegetables\n");
                advice.append("• Choose lean proteins\n");
                advice.append("• Avoid liquid calories (sodas, juices)\n");
                advice.append("• Plan meals ahead to avoid unhealthy choices\n");
            }
        }
        
        // Add a healthy meal idea
        advice.append("\n**Quick Healthy Meal Ideas:**\n");
        advice.append(getRandomMealIdea());
        
        return advice.toString();
    }

    /**
     * Generate steps advice.
     */
    private String generateStepsAdvice(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        
        Optional<StepsLog> todaySteps = stepsLogDAO.findByUserIdAndDate(userId, today);
        int weeklyTotal = stepsLogDAO.getTotalSteps(userId, weekStart, today);
        double weeklyAvg = stepsLogDAO.getAverageSteps(userId, weekStart, today);
        
        StringBuilder advice = new StringBuilder();
        advice.append("👟 **Step Count Analysis**\n\n");
        
        int todayCount = todaySteps.map(StepsLog::getSteps).orElse(0);
        advice.append(String.format("Today: %,d steps\n", todayCount));
        advice.append(String.format("This week: %,d total steps\n", weeklyTotal));
        advice.append(String.format("Weekly average: %,.0f steps/day\n\n", weeklyAvg));
        
        // Progress assessment
        if (todayCount >= 10000) {
            advice.append("🎉 Amazing! You've hit the 10,000 step goal today!\n\n");
        } else if (todayCount >= 7500) {
            advice.append("👏 Great progress! You're almost at 10,000 steps!\n\n");
        } else if (todayCount >= 5000) {
            advice.append("👍 Good start! Keep moving to reach your goal!\n\n");
        } else {
            advice.append("💪 Time to get moving! Even small walks add up!\n\n");
        }
        
        advice.append("**Tips to Increase Daily Steps:**\n");
        advice.append("• Take the stairs instead of elevator\n");
        advice.append("• Walk during phone calls\n");
        advice.append("• Park farther from your destination\n");
        advice.append("• Take a 10-min walk after meals\n");
        advice.append("• Set hourly reminders to move\n");
        advice.append("• Walk while watching TV (during commercials)\n\n");
        
        advice.append("📊 **Health Benefits of Walking:**\n");
        advice.append("• 10,000 steps ≈ 400-500 calories burned\n");
        advice.append("• Reduces risk of heart disease by up to 35%\n");
        advice.append("• Improves mood and reduces stress\n");
        advice.append("• Helps maintain healthy weight\n");
        
        return advice.toString();
    }

    /**
     * Generate calories advice.
     */
    private String generateCaloriesAdvice(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        Optional<CaloriesLog> todayLog = caloriesLogDAO.findByUserIdAndDate(userId, today);
        
        StringBuilder advice = new StringBuilder();
        advice.append("🔥 **Calorie Management Guide**\n\n");
        
        if (todayLog.isPresent()) {
            CaloriesLog log = todayLog.get();
            advice.append(String.format("Today's intake: %,d calories\n", log.getCaloriesConsumed()));
            advice.append(String.format("Today's burn: %,d calories\n", log.getCaloriesBurned()));
            advice.append(String.format("Net calories: %,d\n\n", log.getNetCalories()));
        }
        
        advice.append("**Understanding Calories:**\n");
        advice.append("• Average daily need: 2000-2500 cal (varies by person)\n");
        advice.append("• Weight loss: Create 500 cal deficit/day = 1 lb/week\n");
        advice.append("• Weight gain: Create 500 cal surplus/day = 1 lb/week\n\n");
        
        advice.append("**Calories Burned by Activity (30 min):**\n");
        advice.append("• Walking (3 mph): ~100-150 cal\n");
        advice.append("• Running (5 mph): ~250-300 cal\n");
        advice.append("• Cycling: ~200-300 cal\n");
        advice.append("• Swimming: ~200-400 cal\n");
        advice.append("• Weight training: ~100-200 cal\n");
        advice.append("• HIIT: ~250-400 cal\n\n");
        
        advice.append("**Tips for Calorie Management:**\n");
        advice.append("• Track your food intake consistently\n");
        advice.append("• Don't skip meals - it leads to overeating later\n");
        advice.append("• Eat protein to stay full longer\n");
        advice.append("• Be mindful of liquid calories\n");
        advice.append("• Quality matters as much as quantity!\n");
        
        return advice.toString();
    }

    /**
     * Generate goals response.
     */
    private String generateGoalsResponse(int userId) throws SQLException {
        List<Goal> activeGoals = goalDAO.findActiveByUserId(userId);
        
        StringBuilder response = new StringBuilder();
        response.append("🎯 **Your Fitness Goals**\n\n");
        
        if (activeGoals.isEmpty()) {
            response.append("You don't have any active goals yet!\n\n");
            response.append("**Setting goals helps you:**\n");
            response.append("• Stay motivated and focused\n");
            response.append("• Track your progress\n");
            response.append("• Celebrate achievements\n\n");
            response.append("**Popular fitness goals:**\n");
            response.append("• Walk 10,000 steps daily\n");
            response.append("• Exercise 150 minutes per week\n");
            response.append("• Burn 500 calories through workouts\n");
            response.append("• Reach a target weight\n\n");
            response.append("Ask your trainer to set personalized goals for you!");
        } else {
            response.append(String.format("You have %d active goal(s):\n\n", activeGoals.size()));
            
            for (Goal goal : activeGoals) {
                response.append(String.format("📌 **%s Goal**\n", goal.getGoalType()));
                response.append(String.format("   Target: %.0f\n", goal.getTargetValue()));
                response.append(String.format("   Progress: %.0f (%.1f%%)\n", 
                    goal.getCurrentValue(), goal.getProgressPercentage()));
                response.append(String.format("   Deadline: %s\n\n", goal.getEndDate()));
                
                // Add encouragement based on progress
                double progress = goal.getProgressPercentage();
                if (progress >= 75) {
                    response.append("   🔥 You're almost there! Keep pushing!\n");
                } else if (progress >= 50) {
                    response.append("   💪 Halfway there! Great progress!\n");
                } else if (progress >= 25) {
                    response.append("   👍 Good start! Keep it up!\n");
                } else {
                    response.append("   🌟 Every journey starts with a single step!\n");
                }
                response.append("\n");
            }
        }
        
        return response.toString();
    }

    /**
     * Generate progress summary.
     */
    private String generateProgressSummary(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);
        
        StringBuilder summary = new StringBuilder();
        summary.append("📈 **Your Fitness Progress Summary**\n\n");
        
        // Weekly stats
        summary.append("**This Week:**\n");
        int weeklySteps = stepsLogDAO.getTotalSteps(userId, weekStart, today);
        double avgSteps = stepsLogDAO.getAverageSteps(userId, weekStart, today);
        int workoutCount = workoutDAO.getWorkoutCount(userId, weekStart, today);
        int workoutDuration = workoutDAO.getTotalDuration(userId, weekStart, today);
        int caloriesBurned = workoutDAO.getTotalCaloriesBurned(userId, weekStart, today);
        
        summary.append(String.format("👟 Steps: %,d total (avg %,.0f/day)\n", weeklySteps, avgSteps));
        summary.append(String.format("🏋️ Workouts: %d sessions, %d minutes\n", workoutCount, workoutDuration));
        summary.append(String.format("🔥 Calories burned: %,d\n\n", caloriesBurned));
        
        // Health metrics
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        if (metrics.isPresent()) {
            summary.append("**Health Status:**\n");
            summary.append(String.format("📊 BMI: %.1f (%s)\n\n", 
                metrics.get().getBmi(), metrics.get().getHealthClassification()));
        }
        
        // Goals progress
        List<Goal> activeGoals = goalDAO.findActiveByUserId(userId);
        if (!activeGoals.isEmpty()) {
            summary.append("**Active Goals:**\n");
            for (Goal goal : activeGoals) {
                summary.append(String.format("🎯 %s: %.1f%% complete\n", 
                    goal.getGoalType(), goal.getProgressPercentage()));
            }
            summary.append("\n");
        }
        
        // Encouragement
        summary.append("**Assessment:**\n");
        if (avgSteps >= 10000 && workoutCount >= 3) {
            summary.append("🌟 Excellent! You're crushing it! Keep up the amazing work!");
        } else if (avgSteps >= 7000 || workoutCount >= 2) {
            summary.append("👏 Good progress! You're on the right track. Push a little more!");
        } else {
            summary.append("💪 Room for improvement! Small consistent efforts lead to big results!");
        }
        
        return summary.toString();
    }

    /**
     * Generate motivational message.
     */
    private String generateMotivation(int userId) throws SQLException {
        Random random = new Random();
        List<String> quotes = MOTIVATION_QUOTES.get("general");
        String quote = quotes.get(random.nextInt(quotes.size()));
        
        StringBuilder motivation = new StringBuilder();
        motivation.append("💪 **Here's Some Motivation For You!**\n\n");
        motivation.append(String.format("*\"%s\"*\n\n", quote));
        
        // Add progress reminder if available
        LocalDate today = LocalDate.now();
        int todaySteps = stepsLogDAO.findByUserIdAndDate(userId, today)
            .map(StepsLog::getSteps).orElse(0);
        
        if (todaySteps > 0) {
            motivation.append(String.format("Remember, you've already taken %,d steps today! ", todaySteps));
            motivation.append("Every step counts! 🚶\n\n");
        }
        
        motivation.append("**Quick Motivation Boosters:**\n");
        motivation.append("• Put on your workout clothes - it's half the battle!\n");
        motivation.append("• Start with just 5 minutes - momentum builds\n");
        motivation.append("• Play your favorite upbeat music\n");
        motivation.append("• Think about how good you'll feel after\n");
        motivation.append("• Remember your 'why' - your goals matter!\n\n");
        
        motivation.append("You've got this! 🔥 Now go crush that workout!");
        
        return motivation.toString();
    }

    /**
     * Generate weight loss advice.
     */
    private String generateWeightLossAdvice(int userId) throws SQLException {
        StringBuilder advice = new StringBuilder();
        advice.append("🏃 **Weight Loss Guide**\n\n");
        
        advice.append("**The Science:**\n");
        advice.append("1 pound = 3,500 calories\n");
        advice.append("Safe weight loss = 1-2 pounds per week\n");
        advice.append("This requires a 500-1000 calorie daily deficit\n\n");
        
        advice.append("**Effective Strategies:**\n\n");
        
        advice.append("🥗 **Nutrition:**\n");
        advice.append("• Create a moderate calorie deficit (not too extreme!)\n");
        advice.append("• Increase protein intake (keeps you full)\n");
        advice.append("• Eat more fiber (vegetables, whole grains)\n");
        advice.append("• Reduce processed foods and added sugars\n");
        advice.append("• Practice portion control\n\n");
        
        advice.append("🏋️ **Exercise:**\n");
        advice.append("• Combine cardio and strength training\n");
        advice.append("• Aim for 150-300 min cardio weekly\n");
        advice.append("• Include 2-3 strength sessions weekly\n");
        advice.append("• Increase daily activity (steps, stairs)\n\n");
        
        advice.append("🧠 **Mindset:**\n");
        advice.append("• Focus on sustainable changes\n");
        advice.append("• Don't aim for perfection\n");
        advice.append("• Track progress (not just weight)\n");
        advice.append("• Celebrate non-scale victories!\n\n");
        
        advice.append("💡 **Remember:** Crash diets don't work long-term. Sustainable habits do!");
        
        return advice.toString();
    }

    /**
     * Generate muscle gain advice.
     */
    private String generateMuscleGainAdvice(int userId) {
        StringBuilder advice = new StringBuilder();
        advice.append("💪 **Muscle Building Guide**\n\n");
        
        advice.append("**The Basics:**\n");
        advice.append("• Progressive overload is key\n");
        advice.append("• Muscles grow during rest\n");
        advice.append("• Nutrition fuels growth\n\n");
        
        advice.append("🏋️ **Training:**\n");
        advice.append("• Focus on compound exercises:\n");
        advice.append("  - Squats, Deadlifts, Bench Press\n");
        advice.append("  - Rows, Pull-ups, Overhead Press\n");
        advice.append("• Train each muscle group 2x/week\n");
        advice.append("• Progressive overload: increase weight/reps\n");
        advice.append("• Rest 48 hours between muscle groups\n\n");
        
        advice.append("🥗 **Nutrition:**\n");
        advice.append("• Calorie surplus of 300-500 daily\n");
        advice.append("• Protein: 1.6-2.2g per kg body weight\n");
        advice.append("• Don't neglect carbs (energy for workouts)\n");
        advice.append("• Eat protein within 2 hours post-workout\n\n");
        
        advice.append("😴 **Recovery:**\n");
        advice.append("• Get 7-9 hours of sleep\n");
        advice.append("• Rest days are essential\n");
        advice.append("• Stay hydrated\n");
        advice.append("• Manage stress levels\n\n");
        
        advice.append("**Sample Weekly Split:**\n");
        advice.append("• Mon: Chest & Triceps\n");
        advice.append("• Tue: Back & Biceps\n");
        advice.append("• Wed: Rest/Cardio\n");
        advice.append("• Thu: Legs & Core\n");
        advice.append("• Fri: Shoulders & Arms\n");
        advice.append("• Sat/Sun: Rest or Light Activity");
        
        return advice.toString();
    }

    /**
     * Generate sleep advice.
     */
    private String generateSleepAdvice() {
        return """
            😴 **Sleep & Recovery Guide**
            
            **Why Sleep Matters for Fitness:**
            • Muscle repair happens during deep sleep
            • Growth hormone is released during sleep
            • Poor sleep increases hunger hormones
            • Sleep deprivation hinders performance
            
            **Optimal Sleep:**
            • Adults need 7-9 hours per night
            • Consistent sleep schedule is crucial
            • Quality matters as much as quantity
            
            **Sleep Hygiene Tips:**
            • Keep a consistent sleep schedule
            • Avoid screens 1 hour before bed
            • Keep bedroom cool (65-68°F/18-20°C)
            • Limit caffeine after 2 PM
            • Create a relaxing bedtime routine
            • Exercise regularly (but not too late)
            • Avoid large meals before bed
            
            **Recovery Tips:**
            • Take 1-2 rest days per week
            • Include light stretching/yoga
            • Consider foam rolling
            • Listen to your body's signals
            • Stay hydrated throughout the day
            
            💡 **Remember:** Rest is when you get stronger!
            """;
    }

    /**
     * Generate hydration advice.
     */
    private String generateHydrationAdvice() {
        return """
            💧 **Hydration Guide**
            
            **Daily Water Needs:**
            • General: 8 glasses (64 oz / 2 liters)
            • Active: 0.5-1 oz per pound body weight
            • Hot weather: Add 1-2 extra glasses
            • During exercise: 7-10 oz every 10-20 min
            
            **Why Hydration Matters:**
            • Regulates body temperature
            • Lubricates joints
            • Transports nutrients
            • Removes waste products
            • Improves performance & energy
            
            **Signs of Dehydration:**
            • Dark yellow urine
            • Dry mouth and skin
            • Fatigue and dizziness
            • Headaches
            • Decreased performance
            
            **Tips to Stay Hydrated:**
            • Carry a water bottle everywhere
            • Set hourly drink reminders
            • Eat water-rich foods (cucumber, watermelon)
            • Drink a glass before each meal
            • Start your day with water
            • Flavor water with lemon or cucumber
            
            **During Workouts:**
            • Drink before, during, and after
            • For 60+ min: consider electrolytes
            • Weigh yourself before/after to track loss
            
            💡 **Pro Tip:** If you're thirsty, you're already dehydrated!
            """;
    }

    /**
     * Get workout type specific advice.
     */
    private String getWorkoutTypeAdvice(String type) {
        return switch (type) {
            case "cardio" -> """
                🏃 **Cardio Training Guide**
                
                **Benefits:**
                • Strengthens heart and lungs
                • Burns calories effectively
                • Improves endurance
                • Reduces stress
                
                **Types of Cardio:**
                • Low Intensity (Walking, Swimming): 30-60 min
                • Moderate (Jogging, Cycling): 20-40 min
                • High Intensity (Running, HIIT): 15-30 min
                
                **Weekly Recommendations:**
                • 150 min moderate OR 75 min vigorous
                • Mix different types for variety
                • Include rest days
                
                **Tips:**
                • Start slow and build up
                • Warm up 5-10 min before
                • Cool down and stretch after
                • Stay hydrated
                • Track your heart rate
                """;
            case "yoga" -> """
                🧘 **Yoga & Flexibility Guide**
                
                **Benefits:**
                • Improves flexibility
                • Builds strength
                • Reduces stress
                • Enhances balance
                • Prevents injuries
                
                **Types to Try:**
                • Hatha: Great for beginners
                • Vinyasa: Flow-based, moderate intensity
                • Yin: Deep stretching, relaxation
                • Power Yoga: Strength-focused
                
                **Getting Started:**
                • Start with 15-20 min sessions
                • Focus on breathing
                • Don't force stretches
                • Use props if needed
                
                **Key Poses:**
                • Downward Dog
                • Warrior I & II
                • Child's Pose
                • Tree Pose
                • Cat-Cow
                """;
            case "strength" -> """
                🏋️ **Strength Training Guide**
                
                **Benefits:**
                • Builds muscle mass
                • Increases metabolism
                • Strengthens bones
                • Improves posture
                • Boosts confidence
                
                **Essential Exercises:**
                • Squats (legs/glutes)
                • Deadlifts (back/legs)
                • Bench Press (chest)
                • Rows (back)
                • Overhead Press (shoulders)
                • Pull-ups (back/arms)
                
                **Guidelines:**
                • Train each muscle 2x/week
                • 3-4 sets of 8-12 reps
                • Progressive overload
                • Rest 48h between muscle groups
                
                **Form Tips:**
                • Start with lighter weights
                • Focus on proper form first
                • Control the movement
                • Breathe properly
                • Ask for help when needed
                """;
            case "hiit" -> """
                ⚡ **HIIT Training Guide**
                
                **What is HIIT?**
                High-Intensity Interval Training alternates between intense bursts and rest periods.
                
                **Benefits:**
                • Burns more calories in less time
                • Boosts metabolism for hours
                • Improves cardiovascular fitness
                • No equipment needed
                
                **Sample HIIT Workout:**
                • 30 sec Jumping Jacks
                • 30 sec Rest
                • 30 sec Burpees
                • 30 sec Rest
                • 30 sec Mountain Climbers
                • 30 sec Rest
                • Repeat 3-4 times
                
                **Guidelines:**
                • Limit to 2-3 sessions/week
                • Allow full recovery between sessions
                • Warm up thoroughly
                • Scale to your fitness level
                
                **Warning:** HIIT is intense! Start slowly if you're new.
                """;
            default -> "I don't have specific advice for that workout type, but I'd be happy to help with general fitness questions!";
        };
    }

    /**
     * Generate random tip.
     */
    private String generateRandomTip() {
        Random random = new Random();
        String tip = GENERAL_FITNESS_TIPS.get(random.nextInt(GENERAL_FITNESS_TIPS.size()));
        return "💡 **Quick Fitness Tip:**\n\n" + tip + "\n\nWant more tips? Just ask!";
    }

    /**
     * Generate default response.
     */
    private String generateDefaultResponse(String message) {
        return String.format("""
            I'm not quite sure how to help with "%s", but I'm here to assist with your fitness journey! 🏃
            
            Try asking me about:
            • Workout suggestions
            • Diet and nutrition tips
            • Your BMI and health metrics
            • Progress tracking
            • Motivation and tips
            
            Type "help" for a full list of what I can do!
            """, message.length() > 50 ? message.substring(0, 50) + "..." : message);
    }

    /**
     * Check if message matches any of the keywords.
     */
    private boolean matchesPattern(String message, String... keywords) {
        for (String keyword : keywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a random workout routine.
     */
    private String getRandomWorkoutRoutine() {
        List<String> routines = Arrays.asList(
            """
            **Full Body Workout (30 min):**
            • 5 min warm-up (jumping jacks, arm circles)
            • 10 squats x 3 sets
            • 10 push-ups x 3 sets
            • 10 lunges each leg x 2 sets
            • 30 sec plank x 3 sets
            • 5 min cool-down stretching
            """,
            """
            **Cardio Blast (25 min):**
            • 5 min warm-up walk
            • 30 sec sprint, 30 sec walk x 10
            • 5 min moderate jog
            • 5 min cool-down walk
            """,
            """
            **Core & Abs (20 min):**
            • 30 sec plank
            • 15 bicycle crunches
            • 10 leg raises
            • 30 sec side plank each side
            • 15 mountain climbers
            • Repeat 3 times
            """,
            """
            **Upper Body (25 min):**
            • 10 push-ups x 3 sets
            • 10 tricep dips x 3 sets
            • 30 sec wall sit
            • 10 shoulder taps in plank x 3 sets
            • 5 min stretching
            """
        );
        return routines.get(new Random().nextInt(routines.size()));
    }

    /**
     * Get a random healthy meal idea.
     */
    private String getRandomMealIdea() {
        List<String> meals = Arrays.asList(
            """
            🍳 **Breakfast:** Greek yogurt parfait
            - Greek yogurt, berries, granola, honey
            - ~350 calories, 20g protein
            """,
            """
            🥗 **Lunch:** Grilled chicken salad
            - Grilled chicken, mixed greens, veggies, olive oil dressing
            - ~450 calories, 35g protein
            """,
            """
            🍽️ **Dinner:** Salmon with quinoa
            - Baked salmon, quinoa, steamed broccoli
            - ~500 calories, 40g protein
            """,
            """
            🥤 **Post-Workout:** Protein smoothie
            - Banana, protein powder, almond milk, peanut butter
            - ~400 calories, 30g protein
            """
        );
        return meals.get(new Random().nextInt(meals.size()));
    }

    /**
     * Initialize the knowledge base with tips and suggestions.
     */
    private static void initializeKnowledgeBase() {
        // Motivation quotes
        MOTIVATION_QUOTES.put("general", Arrays.asList(
            "The only bad workout is the one that didn't happen.",
            "Success is the sum of small efforts repeated day in and day out.",
            "Your body can stand almost anything. It's your mind you have to convince.",
            "Don't wish for it, work for it.",
            "The pain you feel today will be the strength you feel tomorrow.",
            "Fitness is not about being better than someone else. It's about being better than you used to be.",
            "The difference between try and triumph is a little umph.",
            "Push yourself because no one else is going to do it for you.",
            "Your health is an investment, not an expense.",
            "Sweat is just fat crying.",
            "It never gets easier, you just get stronger.",
            "The hardest step is the first one out the door."
        ));

        // General fitness tips
        GENERAL_FITNESS_TIPS.addAll(Arrays.asList(
            "Take the stairs instead of the elevator whenever possible!",
            "Aim to drink at least 8 glasses of water daily for optimal hydration.",
            "Get 7-9 hours of sleep - it's when your body recovers and builds muscle!",
            "Don't skip warm-up! It prevents injuries and improves performance.",
            "Mix up your workouts to avoid plateaus and keep things interesting.",
            "Listen to your body - rest when you need to.",
            "Consistency beats intensity. Regular moderate exercise is better than occasional intense workouts.",
            "Plan your meals ahead to avoid unhealthy food choices.",
            "Take progress photos - the scale doesn't tell the whole story!",
            "Celebrate small wins - every step towards your goal matters!",
            "Add protein to every meal to help build and repair muscles.",
            "Standing burns 50% more calories than sitting. Try a standing desk!",
            "Park farther away and walk - easy extra steps!",
            "Stretch for 5-10 minutes daily to improve flexibility and reduce injury risk.",
            "Track your food for a week to understand your eating patterns better."
        ));
    }
}
