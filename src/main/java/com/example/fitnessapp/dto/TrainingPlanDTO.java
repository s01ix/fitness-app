package com.example.fitnessapp.dto;

import java.math.BigDecimal;

public class TrainingPlanDTO {
    private final String planName;
    private final String exerciseName;
    private final int sets;
    private final int reps;
    private final BigDecimal weight;
    private final int restSeconds;

    public TrainingPlanDTO(String planName, String exerciseName, int sets, int reps, BigDecimal weight, int restSeconds) {
        this.planName = planName;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restSeconds = restSeconds;
    }

    public String getPlanName() { return planName; }
    public String getExerciseName() { return exerciseName; }
    public int getSets() { return sets; }
    public int getReps() { return reps; }
    public BigDecimal getWeight() { return weight; }
    public int getRestSeconds() { return restSeconds; }
}