package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.GymUser;
import com.example.fitnessapp.dto.GymUserDTO;
import java.util.List;
import java.util.Optional;

public interface GymUserDAO {
    List<GymUserDTO> findAllTrainers();
    List<GymUser> findAll();
    Optional<GymUser> findById(int id);
    Optional<GymUser> findByEmail(String email);
    void save(GymUser user);
    void update(GymUser user);
    void delete(int id);
}
