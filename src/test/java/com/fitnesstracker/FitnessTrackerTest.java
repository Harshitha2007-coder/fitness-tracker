package com.fitnesstracker;

import com.fitnesstracker.model.*;
import com.fitnesstracker.util.PasswordUtils;
import com.fitnesstracker.util.ValidationUtils;
import com.fitnesstracker.service.BMIService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for the Fitness Tracker application.
 */
public class FitnessTrackerTest {

    // ==================== BMI Tests ====================

    @Test
    @DisplayName("BMI calculation should be correct")
    void testBMICalculation() {
        BMIService bmiService = new BMIService();
        
        // 70kg, 175cm -> BMI = 70 / (1.75)^2 = 22.86
        double bmi = bmiService.calculateBMI(70, 175);
        assertEquals(22.86, bmi, 0.1);
        
        // 90kg, 180cm -> BMI = 90 / (1.8)^2 = 27.78
        bmi = bmiService.calculateBMI(90, 180);
        assertEquals(27.78, bmi, 0.1);
    }

    @Test
    @DisplayName("BMI category classification should be correct")
    void testBMIClassification() {
        assertEquals(BMICategory.UNDERWEIGHT, BMICategory.fromBMI(17.0));
        assertEquals(BMICategory.NORMAL, BMICategory.fromBMI(22.0));
        assertEquals(BMICategory.OVERWEIGHT, BMICategory.fromBMI(27.0));
        assertEquals(BMICategory.OBESE, BMICategory.fromBMI(32.0));
    }

    @Test
    @DisplayName("BMI boundary values should be classified correctly")
    void testBMIBoundaries() {
        assertEquals(BMICategory.UNDERWEIGHT, BMICategory.fromBMI(18.4));
        assertEquals(BMICategory.NORMAL, BMICategory.fromBMI(18.5));
        assertEquals(BMICategory.NORMAL, BMICategory.fromBMI(24.9));
        assertEquals(BMICategory.OVERWEIGHT, BMICategory.fromBMI(25.0));
        assertEquals(BMICategory.OVERWEIGHT, BMICategory.fromBMI(29.9));
        assertEquals(BMICategory.OBESE, BMICategory.fromBMI(30.0));
    }

    @Test
    @DisplayName("Non-normal BMI categories should need alert")
    void testBMINeedsAlert() {
        assertTrue(BMICategory.UNDERWEIGHT.needsAlert());
        assertFalse(BMICategory.NORMAL.needsAlert());
        assertTrue(BMICategory.OVERWEIGHT.needsAlert());
        assertTrue(BMICategory.OBESE.needsAlert());
    }

    // ==================== Password Tests ====================

    @Test
    @DisplayName("Password hashing and verification should work")
    void testPasswordHashing() {
        String password = "SecurePass123";
        String hash = PasswordUtils.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(PasswordUtils.verifyPassword(password, hash));
        assertFalse(PasswordUtils.verifyPassword("wrongPassword", hash));
    }

    @Test
    @DisplayName("Password validation rules should be enforced")
    void testPasswordValidation() {
        // Valid passwords
        assertTrue(PasswordUtils.isValidPassword("ValidPass1"));
        assertTrue(PasswordUtils.isValidPassword("MySecure123"));
        
        // Invalid passwords
        assertFalse(PasswordUtils.isValidPassword("short1")); // too short
        assertFalse(PasswordUtils.isValidPassword("alllowercase1")); // no uppercase
        assertFalse(PasswordUtils.isValidPassword("ALLUPPERCASE1")); // no lowercase
        assertFalse(PasswordUtils.isValidPassword("NoDigitsHere")); // no digit
        assertFalse(PasswordUtils.isValidPassword(null)); // null
    }

    @Test
    @DisplayName("Password hashing should reject null or empty passwords")
    void testPasswordHashingRejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtils.hashPassword(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtils.hashPassword(""));
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Email validation should work correctly")
    void testEmailValidation() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.org"));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("@nodomain.com"));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    @Test
    @DisplayName("Username validation should work correctly")
    void testUsernameValidation() {
        assertTrue(ValidationUtils.isValidUsername("john_doe"));
        assertTrue(ValidationUtils.isValidUsername("user123"));
        assertFalse(ValidationUtils.isValidUsername("ab")); // too short
        assertFalse(ValidationUtils.isValidUsername("invalid@user")); // special char
        assertFalse(ValidationUtils.isValidUsername(null));
    }

    @Test
    @DisplayName("Height and weight validation should work correctly")
    void testPhysicalMeasurementValidation() {
        assertTrue(ValidationUtils.isValidHeight(170));
        assertTrue(ValidationUtils.isValidHeight(50)); // min
        assertTrue(ValidationUtils.isValidHeight(300)); // max
        assertFalse(ValidationUtils.isValidHeight(49)); // below min
        assertFalse(ValidationUtils.isValidHeight(301)); // above max
        
        assertTrue(ValidationUtils.isValidWeight(70));
        assertTrue(ValidationUtils.isValidWeight(10)); // min
        assertTrue(ValidationUtils.isValidWeight(500)); // max
        assertFalse(ValidationUtils.isValidWeight(9)); // below min
        assertFalse(ValidationUtils.isValidWeight(501)); // above max
    }

    @Test
    @DisplayName("Step count validation should work correctly")
    void testStepCountValidation() {
        assertTrue(ValidationUtils.isValidStepCount(5000));
        assertTrue(ValidationUtils.isValidStepCount(0));
        assertTrue(ValidationUtils.isValidStepCount(100000));
        assertFalse(ValidationUtils.isValidStepCount(-1));
        assertFalse(ValidationUtils.isValidStepCount(100001));
    }

    @Test
    @DisplayName("Calorie validation should work correctly")
    void testCalorieValidation() {
        assertTrue(ValidationUtils.isValidCalories(500));
        assertTrue(ValidationUtils.isValidCalories(0));
        assertTrue(ValidationUtils.isValidCalories(10000));
        assertFalse(ValidationUtils.isValidCalories(-1));
        assertFalse(ValidationUtils.isValidCalories(10001));
    }

    @Test
    @DisplayName("Duration validation should work correctly")
    void testDurationValidation() {
        assertTrue(ValidationUtils.isValidDuration(30));
        assertTrue(ValidationUtils.isValidDuration(1));
        assertTrue(ValidationUtils.isValidDuration(1440));
        assertFalse(ValidationUtils.isValidDuration(0));
        assertFalse(ValidationUtils.isValidDuration(1441));
    }

    // ==================== Model Tests ====================

    @Test
    @DisplayName("User model should store data correctly")
    void testUserModel() {
        User user = new User("testuser", "test@email.com", "hashedpwd", 
                           UserRole.INDIVIDUAL, "John", "Doe");
        
        assertEquals("testuser", user.getUsername());
        assertEquals("test@email.com", user.getEmail());
        assertEquals(UserRole.INDIVIDUAL, user.getRole());
        assertEquals("John Doe", user.getFullName());
    }

    @Test
    @DisplayName("DailySteps should calculate goal progress correctly")
    void testDailyStepsProgress() {
        DailySteps steps = new DailySteps(1, 7500, LocalDate.now());
        steps.setGoalSteps(10000);
        
        assertEquals(75.0, steps.getGoalProgress(), 0.01);
        assertFalse(steps.isGoalAchieved());
        
        steps.setStepCount(10000);
        assertEquals(100.0, steps.getGoalProgress(), 0.01);
        assertTrue(steps.isGoalAchieved());
        
        steps.setStepCount(12000);
        assertEquals(120.0, steps.getGoalProgress(), 0.01);
        assertTrue(steps.isGoalAchieved());
    }

    @Test
    @DisplayName("HealthMetrics should calculate BMI automatically")
    void testHealthMetricsBMICalculation() {
        HealthMetrics metrics = new HealthMetrics(1, 70, 175, LocalDate.now());
        
        assertEquals(22.86, metrics.getBmi(), 0.1);
        assertEquals(BMICategory.NORMAL, metrics.getBmiCategory());
    }

    @Test
    @DisplayName("FitnessGoal should calculate progress correctly")
    void testFitnessGoalProgress() {
        FitnessGoal goal = new FitnessGoal(1, "WEIGHT_LOSS", 10.0, LocalDate.now().plusMonths(3));
        
        assertEquals(0, goal.getProgressPercentage(), 0.01);
        assertFalse(goal.isCompleted());
        
        goal.setCurrentValue(5.0);
        assertEquals(50.0, goal.getProgressPercentage(), 0.01);
        assertFalse(goal.isCompleted());
        
        goal.setCurrentValue(10.0);
        assertEquals(100.0, goal.getProgressPercentage(), 0.01);
        assertTrue(goal.isCompleted());
    }

    @Test
    @DisplayName("Workout model should store data correctly")
    void testWorkoutModel() {
        Workout workout = new Workout(1, "Running", 45, 450, "HIGH", LocalDate.now());
        
        assertEquals("Running", workout.getWorkoutType());
        assertEquals(45, workout.getDurationMinutes());
        assertEquals(450, workout.getCaloriesBurned());
        assertEquals("HIGH", workout.getIntensity());
    }

    @Test
    @DisplayName("CalorieIntake model should store data correctly")
    void testCalorieIntakeModel() {
        CalorieIntake intake = new CalorieIntake(1, "BREAKFAST", "Oatmeal", 350, LocalDate.now());
        
        assertEquals("BREAKFAST", intake.getMealType());
        assertEquals("Oatmeal", intake.getFoodItem());
        assertEquals(350, intake.getCalories());
    }

    @Test
    @DisplayName("HealthAlert model should have correct defaults")
    void testHealthAlertModel() {
        HealthAlert alert = new HealthAlert();
        
        assertEquals("INFO", alert.getSeverity());
        assertFalse(alert.isRead());
        
        alert = new HealthAlert(1, "BMI_WARNING", "Test message", "WARNING");
        assertEquals("WARNING", alert.getSeverity());
        assertFalse(alert.isRead());
    }

    // ==================== BMI Service Tests ====================

    @Test
    @DisplayName("BMI Service should reject invalid inputs")
    void testBMIServiceValidation() {
        BMIService bmiService = new BMIService();
        
        assertThrows(IllegalArgumentException.class, () -> bmiService.calculateBMI(0, 175));
        assertThrows(IllegalArgumentException.class, () -> bmiService.calculateBMI(70, 0));
        assertThrows(IllegalArgumentException.class, () -> bmiService.calculateBMI(-70, 175));
        assertThrows(IllegalArgumentException.class, () -> bmiService.calculateBMI(70, -175));
    }

    @Test
    @DisplayName("BMI Service should calculate target weight correctly")
    void testTargetWeightCalculation() {
        BMIService bmiService = new BMIService();
        
        // For height 175cm and target BMI 22:
        // Target weight = 22 * (1.75)^2 = 67.375 kg
        double targetWeight = bmiService.getTargetWeight(175, 22);
        assertEquals(67.375, targetWeight, 0.1);
    }
}
