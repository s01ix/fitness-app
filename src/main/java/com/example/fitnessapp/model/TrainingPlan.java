package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class TrainingPlan {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final IntegerProperty trainerId = new SimpleIntegerProperty();
    private final StringProperty goal = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> createdAt = new SimpleObjectProperty<>();

    public TrainingPlan() {}

    public TrainingPlan(int id, int userId, int trainerId, String goal, LocalDate createdAt) {
        this.id.set(id);
        this.userId.set(userId);
        this.trainerId.set(trainerId);
        this.goal.set(goal);
        this.createdAt.set(createdAt);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getUserId() { return userId.get(); }
    public IntegerProperty userIdProperty() { return userId; }
    public void setUserId(int userId) { this.userId.set(userId); }

    public int getTrainerId() { return trainerId.get(); }
    public IntegerProperty trainerIdProperty() { return trainerId; }
    public void setTrainerId(int trainerId) { this.trainerId.set(trainerId); }

    public String getGoal() { return goal.get(); }
    public StringProperty goalProperty() { return goal; }
    public void setGoal(String goal) { this.goal.set(goal); }

    public LocalDate getCreatedAt() { return createdAt.get(); }
    public ObjectProperty<LocalDate> createdAtProperty() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt.set(createdAt); }
}