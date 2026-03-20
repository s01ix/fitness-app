package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Complaint;
import java.util.List;
import java.util.Optional;

public interface ComplaintDAO {
    List<Complaint> findAll();
    List<Complaint> findByAuthorId(int authorId);
    Optional<Complaint> findById(int id);
    void save(Complaint complaint);
    void update(Complaint complaint);
    void delete(int id);
}