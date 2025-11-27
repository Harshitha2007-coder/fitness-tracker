package com.fitnesstracker.util;

/**
 * Utility class for BMI calculation and health classification.
 */
public class BMICalculator {

    /**
     * Calculate BMI from weight (kg) and height (cm).
     * Formula: BMI = weight(kg) / (height(m))^2
     */
    public static double calculateBMI(double weightKg, double heightCm) {
        if (weightKg <= 0 || heightCm <= 0) {
            throw new IllegalArgumentException("Weight and height must be positive values");
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Get health classification based on BMI value.
     * WHO Classification:
     * - Underweight: BMI < 18.5
     * - Normal: 18.5 <= BMI < 25
     * - Overweight: 25 <= BMI < 30
     * - Obese Class I: 30 <= BMI < 35
     * - Obese Class II: 35 <= BMI < 40
     * - Obese Class III: BMI >= 40
     */
    public static String getHealthClassification(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal";
        } else if (bmi < 30) {
            return "Overweight";
        } else if (bmi < 35) {
            return "Obese Class I";
        } else if (bmi < 40) {
            return "Obese Class II";
        } else {
            return "Obese Class III";
        }
    }

    /**
     * Get health risk level based on BMI.
     */
    public static String getHealthRisk(double bmi) {
        if (bmi < 18.5) {
            return "Increased risk of nutritional deficiency and osteoporosis";
        } else if (bmi < 25) {
            return "Low risk - Healthy weight range";
        } else if (bmi < 30) {
            return "Moderate risk of cardiovascular disease";
        } else if (bmi < 35) {
            return "High risk of cardiovascular disease and diabetes";
        } else if (bmi < 40) {
            return "Very high risk of serious health conditions";
        } else {
            return "Extremely high risk - Immediate medical attention recommended";
        }
    }

    /**
     * Get recommended weight range for a given height.
     */
    public static double[] getRecommendedWeightRange(double heightCm) {
        double heightM = heightCm / 100.0;
        double minWeight = 18.5 * heightM * heightM;
        double maxWeight = 24.9 * heightM * heightM;
        return new double[]{minWeight, maxWeight};
    }

    /**
     * Calculate ideal weight for a given height (using Hamwi formula).
     */
    public static double calculateIdealWeight(double heightCm, boolean isMale) {
        double heightInches = heightCm / 2.54;
        double baseHeight = 60; // 5 feet in inches
        double baseWeight;
        double weightPerInch;

        if (isMale) {
            baseWeight = 48; // kg for 5 feet
            weightPerInch = 2.7; // kg per inch over 5 feet
        } else {
            baseWeight = 45; // kg for 5 feet
            weightPerInch = 2.2; // kg per inch over 5 feet
        }

        if (heightInches <= baseHeight) {
            return baseWeight;
        }
        return baseWeight + (heightInches - baseHeight) * weightPerInch;
    }
}
