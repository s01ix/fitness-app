package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.PlanItem;
import java.util.List;

public interface PlanItemDAO {
    List<PlanItem> findByPlanId(int planId);
    void save(PlanItem item);
    void deleteByPlanId(int id);
}