package com.fitnesstracker;

import com.fitnesstracker.model.*;
import com.fitnesstracker.util.BMICalculator;
import com.fitnesstracker.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Fitness Tracker application.
 */
class FitnessTrackerTest {

    // ==================== BMI Calculator Tests ====================

    @Test
    void testBMICalculation() {
        // Test normal BMI calculation
        double bmi = BMICalculator.calculateBMI(70, 175);
        assertEquals(22.86, bmi, 0.1);
    }

    @Test
    void testBMICalculationUnderweight() {
        double bmi = BMICalculator.calculateBMI(50, 175);
        assertEquals(16.33, bmi, 0.1);
        assertEquals("Underweight", BMICalculator.getHealthClassification(bmi));
    }

    @Test
    void testBMICalculationNormal() {
        double bmi = BMICalculator.calculateBMI(70, 175);
        assertEquals("Normal", BMICalculator.getHealthClassification(bmi));
    }

    @Test
    void testBMICalculationOverweight() {
        double bmi = BMICalculator.calculateBMI(85, 175);
        assertEquals("Overweight", BMICalculator.getHealthClassification(bmi));
    }

    @Test
    void testBMICalculationObese() {
        double bmi = BMICalculator.calculateBMI(95, 175);
        assertEquals("Obese Class I", BMICalculator.getHealthClassification(bmi));
    }

    @Test
    void testBMICalculationInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> BMICalculator.calculateBMI(0, 175));
        assertThrows(IllegalArgumentException.class, () -> BMICalculator.calculateBMI(70, 0));
        assertThrows(IllegalArgumentException.class, () -> BMICalculator.calculateBMI(-70, 175));
    }

    @Test
    void testRecommendedWeightRange() {
        double[] range = BMICalculator.getRecommendedWeightRange(175);
        assertTrue(range[0] > 0);
        assertTrue(range[1] > range[0]);
        // For 175cm, healthy range is approximately 56.6-76.3 kg
        assertEquals(56.6, range[0], 0.5);
        assertEquals(76.3, range[1], 0.5);
    }

    @Test
    void testIdealWeight() {
        double idealMale = BMICalculator.calculateIdealWeight(175, true);
        double idealFemale = BMICalculator.calculateIdealWeight(175, false);
        
        assertTrue(idealMale > 0);
        assertTrue(idealFemale > 0);
        assertTrue(idealMale > idealFemale); // Males typically have higher ideal weight
    }

    // ==================== Password Utility Tests ====================

    @Test
    void testPasswordHashing() {
        String password = "TestPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$")); // BCrypt prefix
    }

    @Test
    void testPasswordVerification() {
        String password = "SecurePass456";
        String hash = PasswordUtil.hashPassword(password);
        
        assertTrue(PasswordUtil.verifyPassword(password, hash));
        assertFalse(PasswordUtil.verifyPassword("WrongPassword", hash));
    }

    @Test
    void testDifferentPasswordsProduceDifferentHashes() {
        String hash1 = PasswordUtil.hashPassword("Password1");
        String hash2 = PasswordUtil.hashPassword("Password1");
        
        // Even same passwords should produce different hashes (salt)
        assertNotEquals(hash1, hash2);
    }

    // ==================== Model Tests ====================

    @Test
    void testUserModel() {
        User user = new User("testuser", "hashedpwd", "test@example.com", "Test User", Role.INDIVIDUAL);
        
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getFullName());
        assertEquals(Role.INDIVIDUAL, user.getRole());
    }

    @Test
    void testStepsLogModel() {
        StepsLog log = new StepsLog(1, 10000, LocalDate.now());
        
        assertEquals(1, log.getUserId());
        assertEquals(10000, log.getSteps());
        assertEquals(LocalDate.now(), log.getLogDate());
    }

    @Test
    void testCaloriesLogModel() {
        CaloriesLog log = new CaloriesLog(1, 2000, 500, LocalDate.now());
        
        assertEquals(2000, log.getCaloriesConsumed());
        assertEquals(500, log.getCaloriesBurned());
        assertEquals(1500, log.getNetCalories());
    }

    @Test
    void testWorkoutModel() {
        Workout workout = new Workout(1, "Running", 30, LocalDate.now());
        
        assertEquals("Running", workout.getWorkoutType());
        assertEquals(30, workout.getDurationMinutes());
        assertEquals(Intensity.MEDIUM, workout.getIntensity()); // Default intensity
    }

    @Test
    void testGoalModel() {
        Goal goal = new Goal(1, GoalType.STEPS, 10000, LocalDate.now(), LocalDate.now().plusDays(7));
        
        assertEquals(GoalType.STEPS, goal.getGoalType());
        assertEquals(10000, goal.getTargetValue());
        assertEquals(0, goal.getCurrentValue());
        assertEquals(GoalStatus.IN_PROGRESS, goal.getStatus());
        assertEquals(0, goal.getProgressPercentage());
    }

    @Test
    void testGoalProgressCalculation() {
        Goal goal = new Goal(1, GoalType.STEPS, 10000, LocalDate.now(), LocalDate.now().plusDays(7));
        goal.setCurrentValue(5000);
        
        assertEquals(50.0, goal.getProgressPercentage(), 0.1);
        
        goal.setCurrentValue(10000);
        assertEquals(100.0, goal.getProgressPercentage(), 0.1);
        
        goal.setCurrentValue(12000);
        assertEquals(100.0, goal.getProgressPercentage(), 0.1); // Capped at 100%
    }

    @Test
    void testHealthMetricsModel() {
        HealthMetrics metrics = new HealthMetrics(1, 22.5, "Normal", 70, 175);
        
        assertEquals(22.5, metrics.getBmi());
        assertEquals("Normal", metrics.getHealthClassification());
        assertEquals(70, metrics.getWeightKg());
        assertEquals(175, metrics.getHeightCm());
    }

    @Test
    void testTrainerPlanModel() {
        TrainerPlan plan = new TrainerPlan(1, 2, PlanType.WORKOUT, "Weekly Plan", "Description");
        
        assertEquals(1, plan.getTrainerId());
        assertEquals(2, plan.getClientId());
        assertEquals(PlanType.WORKOUT, plan.getPlanType());
        assertEquals("Weekly Plan", plan.getTitle());
    }

    @Test
    void testAlertModel() {
        Alert alert = new Alert(1, "BMI_NORMAL", "Your BMI is normal!");
        
        assertEquals(1, alert.getUserId());
        assertEquals("BMI_NORMAL", alert.getAlertType());
        assertFalse(alert.isRead());
    }

    // ==================== Enum Tests ====================

    @Test
    void testRoleEnum() {
        assertEquals(2, Role.values().length);
        assertEquals(Role.INDIVIDUAL, Role.valueOf("INDIVIDUAL"));
        assertEquals(Role.TRAINER, Role.valueOf("TRAINER"));
    }

    @Test
    void testGoalTypeEnum() {
        assertEquals(5, GoalType.values().length);
        assertTrue(java.util.Arrays.asList(GoalType.values()).contains(GoalType.STEPS));
        assertTrue(java.util.Arrays.asList(GoalType.values()).contains(GoalType.CALORIES_BURN));
    }

    @Test
    void testIntensityEnum() {
        assertEquals(3, Intensity.values().length);
        assertEquals(Intensity.LOW, Intensity.valueOf("LOW"));
        assertEquals(Intensity.MEDIUM, Intensity.valueOf("MEDIUM"));
        assertEquals(Intensity.HIGH, Intensity.valueOf("HIGH"));
    }

    @Test
    void testGoalStatusEnum() {
        assertEquals(3, GoalStatus.values().length);
        assertEquals(GoalStatus.IN_PROGRESS, GoalStatus.valueOf("IN_PROGRESS"));
        assertEquals(GoalStatus.COMPLETED, GoalStatus.valueOf("COMPLETED"));
        assertEquals(GoalStatus.FAILED, GoalStatus.valueOf("FAILED"));
    }

    @Test
    void testPlanTypeEnum() {
        assertEquals(3, PlanType.values().length);
        assertEquals(PlanType.WORKOUT, PlanType.valueOf("WORKOUT"));
        assertEquals(PlanType.DIET, PlanType.valueOf("DIET"));
        assertEquals(PlanType.GENERAL, PlanType.valueOf("GENERAL"));
    }

    @Test
    void testGenderEnum() {
        assertEquals(3, Gender.values().length);
        assertEquals(Gender.MALE, Gender.valueOf("MALE"));
        assertEquals(Gender.FEMALE, Gender.valueOf("FEMALE"));
        assertEquals(Gender.OTHER, Gender.valueOf("OTHER"));
    }

    // ==================== Edge Case Tests ====================

    @Test
    void testBMIEdgeCases() {
        // Very low BMI
        double veryLowBmi = BMICalculator.calculateBMI(30, 175);
        assertEquals("Underweight", BMICalculator.getHealthClassification(veryLowBmi));
        
        // Very high BMI
        double veryHighBmi = BMICalculator.calculateBMI(200, 175);
        assertEquals("Obese Class III", BMICalculator.getHealthClassification(veryHighBmi));
        
        // Boundary cases
        assertEquals("Normal", BMICalculator.getHealthClassification(18.5));
        assertEquals("Overweight", BMICalculator.getHealthClassification(25.0));
        assertEquals("Obese Class I", BMICalculator.getHealthClassification(30.0));
    }

    @Test
    void testCaloriesNetCalculation() {
        // Positive net (more consumed than burned)
        CaloriesLog log1 = new CaloriesLog(1, 2500, 500, LocalDate.now());
        assertEquals(2000, log1.getNetCalories());
        
        // Negative net (more burned than consumed)
        CaloriesLog log2 = new CaloriesLog(1, 1500, 2000, LocalDate.now());
        assertEquals(-500, log2.getNetCalories());
        
        // Zero net
        CaloriesLog log3 = new CaloriesLog(1, 2000, 2000, LocalDate.now());
        assertEquals(0, log3.getNetCalories());
    }
}
