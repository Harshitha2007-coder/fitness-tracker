package com.fitnesstracker.model;

import java.time.LocalDateTime;

/**
 * ChatMessage model for AI chatbot conversation history.
 */
public class ChatMessage {
    private int id;
    private int userId;
    private String message;
    private String response;
    private LocalDateTime createdAt;

    public ChatMessage() {}

    public ChatMessage(int userId, String message, String response) {
        this.userId = userId;
        this.message = message;
        this.response = response;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", response='" + response + '\'' +
                '}';
    }
}
