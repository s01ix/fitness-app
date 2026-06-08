package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Reservation {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty clientId = new SimpleIntegerProperty();
    private final IntegerProperty classId = new SimpleIntegerProperty();
    private final IntegerProperty trainerId = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> reservationDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();

    public Reservation() {}

    public Reservation(int id, int clientId, int classId, int trainerId,
                       LocalDate reservationDate, String status) {
        this.id.set(id);
        this.clientId.set(clientId);
        this.classId.set(classId);
        this.trainerId.set(trainerId);
        this.reservationDate.set(reservationDate);
        this.status.set(status);
    }

    public Reservation(int clientId, int classId, int trainerId,
                       LocalDate reservationDate, String status) {
        this.clientId.set(clientId);
        this.classId.set(classId);
        this.trainerId.set(trainerId);
        this.reservationDate.set(reservationDate);
        this.status.set(status);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getClientId() {
        return clientId.get();
    }

    public IntegerProperty clientIdProperty() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId.set(clientId);
    }

    public int getClassId() {
        return classId.get();
    }

    public IntegerProperty classIdProperty() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId.set(classId);
    }

    public int getTrainerId() {
        return trainerId.get();
    }

    public IntegerProperty trainerIdProperty() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId.set(trainerId);
    }

    public LocalDate getReservationDate() {
        return reservationDate.get();
    }

    public ObjectProperty<LocalDate> reservationDateProperty() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate.set(reservationDate);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
}