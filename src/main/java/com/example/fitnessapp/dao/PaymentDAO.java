package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentDAO {
    List<Payment> findAll();
    Optional<Payment> findById(int id);
    void save(Payment payment);
    void delete(int id);
}