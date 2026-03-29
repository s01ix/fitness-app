package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.TrainingPlan;
import java.util.List;

public interface TrainingPlanDAO {
    List<TrainingPlan> findByUserId(int userId);
    void save(TrainingPlan plan);
}