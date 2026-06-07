package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.ExerciseDict;
import java.util.List;
import java.util.Optional;

public interface ExerciseDictDAO {
    Optional<ExerciseDict> findById(int id);
    List<ExerciseDict> findAll();
    void save(ExerciseDict exercise);
}