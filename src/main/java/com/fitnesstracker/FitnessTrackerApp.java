package com.fitnesstracker;

import com.fitnesstracker.ui.ConsoleUI;

/**
 * Main entry point for the Fitness Tracker application.
 */
public class FitnessTrackerApp {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║          Welcome to Fitness Tracker Application            ║");
        System.out.println("║   Track your steps, calories, workouts, and health!        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}
