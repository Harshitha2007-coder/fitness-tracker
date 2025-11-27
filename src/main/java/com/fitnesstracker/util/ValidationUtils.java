package com.fitnesstracker.util;

/**
 * Utility class for input validation.
 */
public class ValidationUtils {

    /**
     * Validate email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    /**
     * Validate username format.
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 50) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]+$");
    }

    /**
     * Validate positive number.
     */
    public static boolean isPositiveNumber(double value) {
        return value > 0;
    }

    /**
     * Validate height in centimeters (reasonable range).
     */
    public static boolean isValidHeight(double heightCm) {
        return heightCm >= 50 && heightCm <= 300;
    }

    /**
     * Validate weight in kilograms (reasonable range).
     */
    public static boolean isValidWeight(double weightKg) {
        return weightKg >= 10 && weightKg <= 500;
    }

    /**
     * Validate step count.
     */
    public static boolean isValidStepCount(int steps) {
        return steps >= 0 && steps <= 100000;
    }

    /**
     * Validate calorie value.
     */
    public static boolean isValidCalories(int calories) {
        return calories >= 0 && calories <= 10000;
    }

    /**
     * Validate duration in minutes.
     */
    public static boolean isValidDuration(int minutes) {
        return minutes > 0 && minutes <= 1440; // Max 24 hours
    }

    /**
     * Sanitize string input to prevent basic injection.
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.trim()
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
