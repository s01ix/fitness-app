package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Discount;
import java.util.List;
import java.util.Optional;

public interface DiscountDAO {
    List<Discount> findAll();
    Optional<Discount> findById(int id);
    void save(Discount discount);
    void update(Discount discount);
    void delete(int id);
}

