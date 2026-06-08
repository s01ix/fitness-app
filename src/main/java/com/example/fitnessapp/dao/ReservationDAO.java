package com.example.fitnessapp.dao;

import com.example.fitnessapp.model.Reservation;
import java.util.List;
import java.util.Optional;

public interface ReservationDAO {
    List<Reservation> findAll();
    Optional<Reservation> findById(int id);
    List<Reservation> findByClientId(int clientId);
    List<Reservation> findByClassId(int classId);
    List<Reservation> findByTrainerId(int trainerId);
    List<Reservation> findByStatus(String status);
    void save(Reservation reservation);
    void update(Reservation reservation);
    void delete(int id);
    void cancel(int reservationId);
    boolean hasAvailableSpots(int classId);
    int countConfirmedReservations(int classId);
}