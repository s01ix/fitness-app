package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.PassType;
import java.util.List;
import java.util.Optional;

public interface PassTypeDAO {
    List<PassType> findAll();
    Optional<PassType> findById(int id);
    void save(PassType passType);
    void update(PassType passType);
    void delete(int id);
}