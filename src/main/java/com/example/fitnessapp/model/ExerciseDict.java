package com.example.fitnessapp.model;

import javafx.beans.property.*;

public class ExerciseDict {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty muscleGroup = new SimpleStringProperty();

    public ExerciseDict() {}

    public ExerciseDict(int id, String name, String description, String muscleGroup) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.muscleGroup.set(muscleGroup);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public void setName(String name) { this.name.set(name); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String description) { this.description.set(description); }

    public String getMuscleGroup() { return muscleGroup.get(); }
    public StringProperty muscleGroupProperty() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup.set(muscleGroup); }
}