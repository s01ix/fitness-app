package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Equipment;
import java.util.List;
import java.util.Optional;

public interface EquipmentDAO {
    List<Equipment> findAll();
    List<Equipment> findByClubId(int clubId);
    Optional<Equipment> findById(int id);
    void save(Equipment equipment);
    void update(Equipment equipment);
    void delete(int id);
}
