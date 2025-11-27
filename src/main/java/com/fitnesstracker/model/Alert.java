package com.fitnesstracker.model;

import java.time.LocalDateTime;

/**
 * Alert model for health and fitness notifications.
 */
public class Alert {
    private int id;
    private int userId;
    private String alertType;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Alert() {}

    public Alert(int userId, String alertType, String message) {
        this.userId = userId;
        this.alertType = alertType;
        this.message = message;
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
        return "Alert{" +
                "id=" + id +
                ", userId=" + userId +
                ", alertType='" + alertType + '\'' +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
