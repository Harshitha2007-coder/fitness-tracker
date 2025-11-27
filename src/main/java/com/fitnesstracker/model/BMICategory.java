package com.fitnesstracker.model;

/**
 * Enum representing BMI categories for health classification.
 */
public enum BMICategory {
    UNDERWEIGHT("Underweight", "BMI < 18.5"),
    NORMAL("Normal", "BMI 18.5 - 24.9"),
    OVERWEIGHT("Overweight", "BMI 25 - 29.9"),
    OBESE("Obese", "BMI >= 30");

    private final String displayName;
    private final String range;

    BMICategory(String displayName, String range) {
        this.displayName = displayName;
        this.range = range;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRange() {
        return range;
    }

    public static BMICategory fromBMI(double bmi) {
        if (bmi < 18.5) {
            return UNDERWEIGHT;
        } else if (bmi < 25) {
            return NORMAL;
        } else if (bmi < 30) {
            return OVERWEIGHT;
        } else {
            return OBESE;
        }
    }

    public boolean needsAlert() {
        return this != NORMAL;
    }

    public String getHealthAdvice() {
        switch (this) {
            case UNDERWEIGHT:
                return "Consider increasing calorie intake with nutrient-rich foods. " +
                       "Consult a healthcare provider for personalized advice.";
            case NORMAL:
                return "Maintain your healthy lifestyle with balanced diet and regular exercise.";
            case OVERWEIGHT:
                return "Consider increasing physical activity and monitoring calorie intake. " +
                       "Small lifestyle changes can make a big difference.";
            case OBESE:
                return "It's recommended to consult a healthcare provider for a personalized " +
                       "weight management plan. Focus on gradual, sustainable changes.";
            default:
                return "Consult a healthcare provider for personalized advice.";
        }
    }
}
