package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Club;

import java.util.List;
import java.util.Optional;

public interface ClubDAO {
    List<Club> findAll();
    Optional<Club> findById(int id);
    void save(Club club);
    void update(Club club);
    void delete(int id);
}
