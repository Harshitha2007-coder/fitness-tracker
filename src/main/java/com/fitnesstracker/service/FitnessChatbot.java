package com.fitnesstracker.service;

import com.fitnesstracker.dao.*;
import com.fitnesstracker.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * AI Chatbot service for fitness tips, workout/diet suggestions, and encouragement.
 */
public class FitnessChatbot {
    
    private final HealthMetricsDAO healthMetricsDAO;
    private final WorkoutDAO workoutDAO;
    private final DailyStepsDAO dailyStepsDAO;
    private final CalorieIntakeDAO calorieIntakeDAO;
    private final BMIService bmiService;
    
    private static final Random random = new Random();

    public FitnessChatbot() {
        this.healthMetricsDAO = new HealthMetricsDAO();
        this.workoutDAO = new WorkoutDAO();
        this.dailyStepsDAO = new DailyStepsDAO();
        this.calorieIntakeDAO = new CalorieIntakeDAO();
        this.bmiService = new BMIService();
    }

    /**
     * Process user message and generate response.
     */
    public String chat(int userId, String userMessage) throws SQLException {
        String message = userMessage.toLowerCase().trim();
        
        // Greeting responses
        if (containsAny(message, "hello", "hi", "hey", "greetings")) {
            return getGreeting(userId);
        }
        
        // Fitness tips
        if (containsAny(message, "tip", "advice", "suggest", "recommendation")) {
            if (containsAny(message, "workout", "exercise", "training")) {
                return getWorkoutTip(userId);
            } else if (containsAny(message, "diet", "food", "eat", "nutrition", "meal")) {
                return getDietTip(userId);
            } else {
                return getGeneralFitnessTip();
            }
        }
        
        // Workout suggestions
        if (containsAny(message, "workout", "exercise", "training")) {
            return getWorkoutSuggestion(userId);
        }
        
        // Diet suggestions
        if (containsAny(message, "diet", "food", "eat", "nutrition", "meal", "calorie")) {
            return getDietSuggestion(userId);
        }
        
        // BMI and health
        if (containsAny(message, "bmi", "weight", "health", "body mass")) {
            return getHealthInfo(userId);
        }
        
        // Progress and stats
        if (containsAny(message, "progress", "stats", "statistics", "how am i doing")) {
            return getProgressSummary(userId);
        }
        
        // Motivation
        if (containsAny(message, "motivation", "motivate", "encourage", "inspire", "tired", "quit")) {
            return getMotivation();
        }
        
        // Steps
        if (containsAny(message, "step", "walk", "walking")) {
            return getStepsInfo(userId);
        }
        
        // Goals
        if (containsAny(message, "goal", "target", "objective")) {
            return getGoalAdvice(userId);
        }
        
        // Help
        if (containsAny(message, "help", "what can you do", "commands", "options")) {
            return getHelpMessage();
        }
        
        // Default response
        return getDefaultResponse();
    }

    private String getGreeting(int userId) throws SQLException {
        String[] greetings = {
            "Hello! I'm your fitness assistant. How can I help you today?",
            "Hi there! Ready to crush your fitness goals?",
            "Hey! Great to see you. What would you like to know about your fitness journey?",
            "Greetings! I'm here to help you stay fit and healthy. What's on your mind?"
        };
        
        StringBuilder response = new StringBuilder(greetings[random.nextInt(greetings.length)]);
        
        // Add personalized info if available
        Optional<DailySteps> todaySteps = dailyStepsDAO.findByUserIdAndDate(userId, LocalDate.now());
        if (todaySteps.isPresent()) {
            DailySteps steps = todaySteps.get();
            response.append("\n\n📊 Quick update: You've taken ")
                   .append(steps.getStepCount())
                   .append(" steps today (")
                   .append(String.format("%.0f%%", steps.getGoalProgress()))
                   .append(" of your goal).");
        }
        
        return response.toString();
    }

    private String getWorkoutTip(int userId) throws SQLException {
        List<String> tips = new ArrayList<>(Arrays.asList(
            "💪 Try to include both cardio and strength training in your weekly routine for balanced fitness.",
            "🏃 High-Intensity Interval Training (HIIT) can burn more calories in less time.",
            "🧘 Don't forget to stretch! Flexibility training reduces injury risk.",
            "💦 Stay hydrated during workouts - drink water before, during, and after exercise.",
            "😴 Rest days are important! Your muscles grow and recover during rest.",
            "📈 Progressive overload is key - gradually increase weight, reps, or duration.",
            "🌅 Morning workouts can boost your metabolism for the whole day.",
            "🎵 Music can improve workout performance by up to 15%!"
        ));
        
        // Add personalized tip based on recent activity
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        int recentWorkouts = workoutDAO.getWorkoutCount(userId, weekAgo, LocalDate.now());
        
        if (recentWorkouts == 0) {
            tips.add("🚀 You haven't logged any workouts this week. Start small - even a 15-minute walk counts!");
        } else if (recentWorkouts >= 5) {
            tips.add("🌟 Great consistency! You've worked out " + recentWorkouts + " times this week. Consider adding variety to prevent plateaus.");
        }
        
        return tips.get(random.nextInt(tips.size()));
    }

    private String getDietTip(int userId) throws SQLException {
        List<String> tips = new ArrayList<>(Arrays.asList(
            "🥗 Fill half your plate with vegetables for balanced nutrition.",
            "💧 Drink a glass of water before meals to help control portion sizes.",
            "🍎 Choose whole fruits over fruit juices to get more fiber.",
            "🥚 Protein at breakfast helps control hunger throughout the day.",
            "🍽️ Eating slowly helps you recognize fullness and prevents overeating.",
            "🚫 Limit processed foods - they often contain hidden sugars and sodium.",
            "🥜 Healthy fats from nuts, avocados, and olive oil are essential for your body.",
            "📝 Track your meals to become more aware of your eating habits."
        ));
        
        // Add personalized tip based on BMI
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        if (metrics.isPresent()) {
            BMICategory category = metrics.get().getBmiCategory();
            switch (category) {
                case UNDERWEIGHT:
                    tips.add("🍗 Since you're underweight, focus on calorie-dense nutritious foods like nuts, nut butters, and whole grains.");
                    break;
                case OVERWEIGHT:
                case OBESE:
                    tips.add("🥦 Focus on high-volume, low-calorie foods like vegetables and lean proteins to feel full while managing calories.");
                    break;
                default:
                    break;
            }
        }
        
        return tips.get(random.nextInt(tips.size()));
    }

    private String getGeneralFitnessTip() {
        String[] tips = {
            "🎯 Set SMART goals: Specific, Measurable, Achievable, Relevant, Time-bound.",
            "📱 Use this app daily to track your progress - consistency is key!",
            "👫 Find a workout buddy - accountability partners increase success rates.",
            "🛏️ Aim for 7-9 hours of quality sleep for optimal recovery and performance.",
            "😰 Manage stress through meditation or deep breathing - stress affects fitness.",
            "📊 Track not just weight, but also measurements, energy levels, and mood.",
            "🏆 Celebrate small wins! Every healthy choice matters.",
            "🔄 Mix up your routine every 4-6 weeks to prevent plateaus."
        };
        return tips[random.nextInt(tips.length)];
    }

    private String getWorkoutSuggestion(int userId) throws SQLException {
        StringBuilder suggestion = new StringBuilder("🏋️ Here's a workout suggestion for you:\n\n");
        
        // Check BMI category for personalized suggestion
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        BMICategory category = metrics.map(HealthMetrics::getBmiCategory).orElse(BMICategory.NORMAL);
        
        switch (category) {
            case UNDERWEIGHT:
                suggestion.append("Focus on strength training to build muscle:\n");
                suggestion.append("• Compound exercises: squats, deadlifts, bench press\n");
                suggestion.append("• 3 sets of 8-12 reps\n");
                suggestion.append("• Rest 60-90 seconds between sets\n");
                suggestion.append("• 3-4 days per week\n");
                suggestion.append("\n💡 Tip: Eat a protein-rich meal within 30 minutes after workout.");
                break;
                
            case OVERWEIGHT:
            case OBESE:
                suggestion.append("Start with low-impact cardio combined with strength:\n");
                suggestion.append("• 20-30 min walking or swimming\n");
                suggestion.append("• Basic bodyweight exercises\n");
                suggestion.append("• Start with 2-3 days per week\n");
                suggestion.append("• Gradually increase intensity\n");
                suggestion.append("\n💡 Tip: Listen to your body and progress slowly to prevent injury.");
                break;
                
            default:
                String[] workouts = {
                    "Full Body Circuit:\n• 10 squats\n• 10 push-ups\n• 10 lunges each leg\n• 30-second plank\n• Repeat 3 times",
                    "Cardio Mix:\n• 5 min warm-up walk\n• 20 min jog/run intervals\n• 5 min cool-down\n• Stretching",
                    "Upper Body Focus:\n• 3x12 push-ups\n• 3x12 dumbbell rows\n• 3x12 shoulder press\n• 3x12 bicep curls",
                    "Lower Body Focus:\n• 3x15 squats\n• 3x12 lunges\n• 3x15 calf raises\n• 3x20 glute bridges"
                };
                suggestion.append(workouts[random.nextInt(workouts.length)]);
        }
        
        return suggestion.toString();
    }

    private String getDietSuggestion(int userId) throws SQLException {
        StringBuilder suggestion = new StringBuilder("🥗 Diet Suggestions:\n\n");
        
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        BMICategory category = metrics.map(HealthMetrics::getBmiCategory).orElse(BMICategory.NORMAL);
        
        switch (category) {
            case UNDERWEIGHT:
                suggestion.append("Goal: Healthy weight gain\n\n");
                suggestion.append("Sample Day:\n");
                suggestion.append("🌅 Breakfast: Oatmeal with banana, nuts, and honey (400 cal)\n");
                suggestion.append("🍎 Snack: Greek yogurt with granola (250 cal)\n");
                suggestion.append("☀️ Lunch: Grilled chicken sandwich with avocado (550 cal)\n");
                suggestion.append("🍌 Snack: Protein smoothie with peanut butter (350 cal)\n");
                suggestion.append("🌙 Dinner: Salmon with quinoa and vegetables (600 cal)\n");
                suggestion.append("\n📊 Target: ~2,500-2,800 calories/day");
                break;
                
            case OVERWEIGHT:
            case OBESE:
                suggestion.append("Goal: Healthy weight loss\n\n");
                suggestion.append("Sample Day:\n");
                suggestion.append("🌅 Breakfast: Egg white omelet with vegetables (250 cal)\n");
                suggestion.append("🍎 Snack: Apple with almond butter (150 cal)\n");
                suggestion.append("☀️ Lunch: Large salad with grilled chicken (400 cal)\n");
                suggestion.append("🥕 Snack: Carrot sticks with hummus (100 cal)\n");
                suggestion.append("🌙 Dinner: Baked fish with roasted vegetables (450 cal)\n");
                suggestion.append("\n📊 Target: ~1,500-1,800 calories/day");
                break;
                
            default:
                suggestion.append("Goal: Maintain healthy weight\n\n");
                suggestion.append("Sample Day:\n");
                suggestion.append("🌅 Breakfast: Whole grain toast with eggs (350 cal)\n");
                suggestion.append("🍎 Snack: Mixed nuts and fruit (200 cal)\n");
                suggestion.append("☀️ Lunch: Turkey wrap with vegetables (450 cal)\n");
                suggestion.append("🥛 Snack: Cottage cheese with berries (150 cal)\n");
                suggestion.append("🌙 Dinner: Lean beef stir-fry with brown rice (550 cal)\n");
                suggestion.append("\n📊 Target: ~2,000-2,200 calories/day");
        }
        
        return suggestion.toString();
    }

    private String getHealthInfo(int userId) throws SQLException {
        Optional<HealthMetrics> metricsOpt = healthMetricsDAO.findLatestByUserId(userId);
        
        if (metricsOpt.isEmpty()) {
            return "📊 I don't have your health metrics yet. Please record your weight and height in the Health Metrics section to get personalized advice!";
        }
        
        HealthMetrics metrics = metricsOpt.get();
        StringBuilder info = new StringBuilder("📊 Your Health Summary:\n\n");
        
        info.append("Weight: ").append(String.format("%.1f kg", metrics.getWeightKg())).append("\n");
        info.append("Height: ").append(String.format("%.0f cm", metrics.getHeightCm())).append("\n");
        info.append("BMI: ").append(String.format("%.1f", metrics.getBmi())).append("\n");
        info.append("Category: ").append(metrics.getBmiCategory().getDisplayName()).append("\n\n");
        info.append("💡 ").append(metrics.getBmiCategory().getHealthAdvice());
        
        // Add ideal weight range
        info.append("\n\n📏 ").append(bmiService.getIdealWeightRange(metrics.getHeightCm()));
        
        return info.toString();
    }

    private String getProgressSummary(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(7);
        
        StringBuilder summary = new StringBuilder("📈 Your Weekly Progress:\n\n");
        
        // Steps
        int totalSteps = dailyStepsDAO.getTotalSteps(userId, weekAgo, today);
        double avgSteps = dailyStepsDAO.getAverageSteps(userId, weekAgo, today);
        int daysGoalMet = dailyStepsDAO.getDaysGoalAchieved(userId, weekAgo, today);
        
        summary.append("🚶 Steps:\n");
        summary.append("   • Total: ").append(String.format("%,d", totalSteps)).append("\n");
        summary.append("   • Daily avg: ").append(String.format("%,.0f", avgSteps)).append("\n");
        summary.append("   • Goal achieved: ").append(daysGoalMet).append(" days\n\n");
        
        // Workouts
        int workoutCount = workoutDAO.getWorkoutCount(userId, weekAgo, today);
        int totalDuration = workoutDAO.getTotalDuration(userId, weekAgo, today);
        int caloriesBurned = workoutDAO.getTotalCaloriesBurned(userId, weekAgo, today);
        
        summary.append("🏋️ Workouts:\n");
        summary.append("   • Sessions: ").append(workoutCount).append("\n");
        summary.append("   • Total duration: ").append(totalDuration).append(" min\n");
        summary.append("   • Calories burned: ").append(String.format("%,d", caloriesBurned)).append("\n\n");
        
        // Encouragement based on activity
        if (workoutCount >= 4 && avgSteps >= 8000) {
            summary.append("🌟 Amazing work! You're crushing your fitness goals!");
        } else if (workoutCount >= 2 || avgSteps >= 5000) {
            summary.append("👍 Good progress! Keep pushing to reach your full potential!");
        } else {
            summary.append("💪 Let's pick up the pace! Every step counts towards your goals!");
        }
        
        return summary.toString();
    }

    private String getMotivation() {
        String[] quotes = {
            "💪 \"The only bad workout is the one that didn't happen.\"",
            "🌟 \"Your body can stand almost anything. It's your mind you have to convince.\"",
            "🔥 \"Don't stop when you're tired. Stop when you're done.\"",
            "🎯 \"The pain you feel today will be the strength you feel tomorrow.\"",
            "🚀 \"Success is the sum of small efforts repeated day in and day out.\"",
            "💫 \"You don't have to be great to start, but you have to start to be great.\"",
            "🏆 \"The difference between try and triumph is a little 'umph'.\"",
            "⭐ \"Every champion was once a contender who refused to give up.\"",
            "🌈 \"Fitness is not about being better than someone else. It's about being better than you used to be.\"",
            "💎 \"Your health is an investment, not an expense.\""
        };
        
        String quote = quotes[random.nextInt(quotes.length)];
        return quote + "\n\n🎯 Remember: Progress, not perfection. You've got this!";
    }

    private String getStepsInfo(int userId) throws SQLException {
        Optional<DailySteps> todaySteps = dailyStepsDAO.findByUserIdAndDate(userId, LocalDate.now());
        
        StringBuilder info = new StringBuilder("🚶 Step Information:\n\n");
        
        if (todaySteps.isPresent()) {
            DailySteps steps = todaySteps.get();
            info.append("Today's steps: ").append(String.format("%,d", steps.getStepCount())).append("\n");
            info.append("Goal: ").append(String.format("%,d", steps.getGoalSteps())).append("\n");
            info.append("Progress: ").append(String.format("%.1f%%", steps.getGoalProgress())).append("\n\n");
            
            int remaining = steps.getGoalSteps() - steps.getStepCount();
            if (remaining > 0) {
                info.append("📍 You need ").append(String.format("%,d", remaining)).append(" more steps to reach your goal.\n");
                info.append("💡 That's about ").append(remaining / 100).append(" minutes of walking!");
            } else {
                info.append("🎉 Congratulations! You've reached your step goal for today!");
            }
        } else {
            info.append("No steps recorded today yet.\n\n");
            info.append("💡 Tip: Aim for 10,000 steps daily for optimal health benefits.\n");
            info.append("📊 10,000 steps ≈ 5 miles ≈ 400-500 calories burned");
        }
        
        return info.toString();
    }

    private String getGoalAdvice(int userId) throws SQLException {
        Optional<HealthMetrics> metrics = healthMetricsDAO.findLatestByUserId(userId);
        
        StringBuilder advice = new StringBuilder("🎯 Goal Setting Advice:\n\n");
        
        if (metrics.isPresent()) {
            BMICategory category = metrics.get().getBmiCategory();
            advice.append("Based on your BMI category (").append(category.getDisplayName()).append("):\n\n");
            
            switch (category) {
                case UNDERWEIGHT:
                    advice.append("Recommended Goals:\n");
                    advice.append("• Gain 0.5 kg per week through healthy eating\n");
                    advice.append("• Build muscle with 3-4 strength sessions/week\n");
                    advice.append("• Increase daily calorie intake by 300-500\n");
                    break;
                case NORMAL:
                    advice.append("Recommended Goals:\n");
                    advice.append("• Maintain current healthy weight\n");
                    advice.append("• 150+ minutes of exercise per week\n");
                    advice.append("• 10,000 daily steps\n");
                    advice.append("• Build strength and endurance\n");
                    break;
                case OVERWEIGHT:
                case OBESE:
                    advice.append("Recommended Goals:\n");
                    advice.append("• Lose 0.5-1 kg per week sustainably\n");
                    advice.append("• 200+ minutes of exercise per week\n");
                    advice.append("• 12,000+ daily steps\n");
                    advice.append("• Reduce daily calories by 500\n");
                    break;
            }
        } else {
            advice.append("General Goal Guidelines:\n");
            advice.append("• Start with achievable short-term goals\n");
            advice.append("• Track your progress regularly\n");
            advice.append("• Adjust goals as you improve\n");
            advice.append("• Celebrate small victories!\n");
        }
        
        advice.append("\n📝 Record your health metrics to get personalized goals!");
        return advice.toString();
    }

    private String getHelpMessage() {
        return "🤖 I'm your AI Fitness Assistant! Here's what I can help you with:\n\n" +
               "💬 Try asking me about:\n" +
               "• \"workout suggestions\" - Get personalized exercise ideas\n" +
               "• \"diet tips\" - Nutrition advice based on your goals\n" +
               "• \"my BMI\" or \"health info\" - View your health metrics\n" +
               "• \"progress\" or \"stats\" - See your weekly summary\n" +
               "• \"step goal\" - Check your daily step progress\n" +
               "• \"motivation\" - Get inspired to keep going\n" +
               "• \"fitness tips\" - General health advice\n" +
               "• \"goals\" - Goal-setting recommendations\n\n" +
               "Just type naturally and I'll do my best to help! 🌟";
    }

    private String getDefaultResponse() {
        String[] responses = {
            "I'm not sure I understood that. Try asking about workouts, diet, your BMI, or type 'help' for options!",
            "Could you rephrase that? I can help with fitness tips, workout suggestions, diet advice, and more!",
            "I didn't quite catch that. Ask me about your progress, health metrics, or say 'help' to see what I can do!",
            "Hmm, I'm not sure about that. Try asking for workout tips, diet suggestions, or motivation!"
        };
        return responses[random.nextInt(responses.length)];
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
