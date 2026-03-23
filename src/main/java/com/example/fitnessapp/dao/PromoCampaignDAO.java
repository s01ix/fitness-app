package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.PromoCampaign;
import java.util.List;
import java.util.Optional;

public interface PromoCampaignDAO {
    List<PromoCampaign> findAll();
    Optional<PromoCampaign> findById(int id);
    void save(PromoCampaign promoCampaign);
    void update(PromoCampaign promoCampaign);
    void delete(int id);
}