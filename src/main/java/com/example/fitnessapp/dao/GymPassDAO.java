package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.GymPass;
import java.util.List;
import java.util.Optional;

public interface GymPassDAO {
    List<GymPass> findAll();
    Optional<GymPass> findById(int id);
    void save(GymPass gymPass);
    void update(GymPass gymPass);
    void delete(int id);
}