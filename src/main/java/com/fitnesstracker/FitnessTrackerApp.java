package com.fitnesstracker;

import com.fitnesstracker.ui.ConsoleUI;

/**
 * Main entry point for the Fitness Tracker application.
 * 
 * A Java-based fitness tracker with MySQL backend for individuals to log steps,
 * calories, workouts, and duration, while trainers monitor progress, analyze trends,
 * and suggest personalized plans.
 * 
 * Features include:
 * - Secure role-based login (Individual/Trainer)
 * - Dynamic dashboards
 * - BMI-based health classification with alerts and personalized goals
 * - AI chatbot for fitness tips, workout and diet suggestions
 */
public class FitnessTrackerApp {
    
    public static void main(String[] args) {
        try {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } catch (Exception e) {
            System.err.println("Application error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
