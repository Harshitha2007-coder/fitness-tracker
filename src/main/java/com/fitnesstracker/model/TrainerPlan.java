package com.fitnesstracker.model;

import java.time.LocalDateTime;

/**
 * TrainerPlan model for workout/diet plans created by trainers for clients.
 */
public class TrainerPlan {
    private int id;
    private int trainerId;
    private int clientId;
    private PlanType planType;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public TrainerPlan() {}

    public TrainerPlan(int trainerId, int clientId, PlanType planType, String title, String description) {
        this.trainerId = trainerId;
        this.clientId = clientId;
        this.planType = planType;
        this.title = title;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public void setPlanType(PlanType planType) {
        this.planType = planType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TrainerPlan{" +
                "id=" + id +
                ", trainerId=" + trainerId +
                ", clientId=" + clientId +
                ", planType=" + planType +
                ", title='" + title + '\'' +
                '}';
    }
}
