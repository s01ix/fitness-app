package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.TrainingPlan;
import java.util.List;
import java.util.Optional;

public interface TrainingPlanDAO {
    List<TrainingPlan> findByUserId(int userId);
    void save(TrainingPlan plan);
    Optional<TrainingPlan> findById(int id);
    void update(TrainingPlan plan);
}