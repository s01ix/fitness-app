package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.CampaignPass;

import java.util.List;
import java.util.Optional;

public interface CampaignPassDAO {
    List<CampaignPass> findAll();
    Optional<CampaignPass> findById(int id);
    void save(CampaignPass campaignPass);
    void update(CampaignPass campaignPass);
    void delete(int id);
}