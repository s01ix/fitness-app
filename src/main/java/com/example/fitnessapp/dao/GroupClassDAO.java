package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.GroupClass;
import java.util.List;
import java.util.Optional;

public interface GroupClassDAO {
    List<GroupClass> findAll();
    Optional<GroupClass> findById(int id);
    void save(GroupClass groupClass);
    void update(GroupClass groupClass);
    void delete(int id);

}
