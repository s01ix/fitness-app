package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.ExerciseDict;
import java.util.List;

public interface ExerciseDictDAO {
    List<ExerciseDict> findAll();
}