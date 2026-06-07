package com.example.fitnessapp.model;

import javafx.beans.property.*;

public class PlanItem {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty planId = new SimpleIntegerProperty();
    private final IntegerProperty exerciseId = new SimpleIntegerProperty();
    private final IntegerProperty sets = new SimpleIntegerProperty();
    private final IntegerProperty reps = new SimpleIntegerProperty();

    public PlanItem() {}

    public PlanItem(int id, int planId, int exerciseId, int sets, int reps) {
        this.id.set(id);
        this.planId.set(planId);
        this.exerciseId.set(exerciseId);
        this.sets.set(sets);
        this.reps.set(reps);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getPlanId() { return planId.get(); }
    public IntegerProperty planIdProperty() { return planId; }
    public void setPlanId(int planId) { this.planId.set(planId); }

    public int getExerciseId() { return exerciseId.get(); }
    public IntegerProperty exerciseIdProperty() { return exerciseId; }
    public void setExerciseId(int exerciseId) { this.exerciseId.set(exerciseId); }

    public int getSets() { return sets.get(); }
    public IntegerProperty setsProperty() { return sets; }
    public void setSets(int sets) { this.sets.set(sets); }

    public int getReps() { return reps.get(); }
    public IntegerProperty repsProperty() { return reps; }
    public void setReps(int reps) { this.reps.set(reps); }

}