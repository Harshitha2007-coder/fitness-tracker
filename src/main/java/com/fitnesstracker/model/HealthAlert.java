package com.fitnesstracker.model;

import java.time.LocalDateTime;

/**
 * HealthAlert model for BMI warnings and health notifications.
 */
public class HealthAlert {
    private int id;
    private int userId;
    private String alertType;
    private String message;
    private String severity;
    private boolean isRead;
    private LocalDateTime createdAt;

    public HealthAlert() {
        this.severity = "INFO";
        this.isRead = false;
    }

    public HealthAlert(int userId, String alertType, String message, String severity) {
        this.userId = userId;
        this.alertType = alertType;
        this.message = message;
        this.severity = severity;
        this.isRead = false;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "[" + severity + "] " + alertType + ": " + message;
    }
}
