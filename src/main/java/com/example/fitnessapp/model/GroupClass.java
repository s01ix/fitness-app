package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.time.LocalDateTime;
public class GroupClass {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty clubId = new SimpleIntegerProperty();
    private final IntegerProperty trainerId = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<LocalDateTime> scheduleTime = new SimpleObjectProperty<>();
    private final IntegerProperty capacity = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();

    public GroupClass(){}

    public GroupClass(int id, int clubId, int trainerId, String name, LocalDateTime scheduleTime, int capacity, String status){
        this.id.set(id);
        this.clubId.set(clubId);
        this.trainerId.set(trainerId);
        this.name.set(name);
        this.scheduleTime.set(scheduleTime);
        this.capacity.set(capacity);
        this.status.set(status);
    }

    public int getId() {return id.get();}
    public IntegerProperty idProperty() {return id;}
    public void setId(int id) {this.id.set(id);}

    public int getClubId() {return  clubId.get();}
    public IntegerProperty clubIdProperty() {return clubId;}
    public void setClubId(int clubId) {this.clubId.set(clubId);}

    public int getTrainerId() {return  trainerId.get();}
    public IntegerProperty trainerIdProperty() {return trainerId;}
    public void setTrainerId(int trainerId) {this.trainerId.set(trainerId);}

    public String getName() {return name.get();}
    public StringProperty nameProperty() {return name;}
    public void setName(String name) {this.name.set(name);}

    public LocalDateTime getScheduleTime(){return scheduleTime.get();}
    public ObjectProperty<LocalDateTime> scheduleTimeProperty() {return scheduleTime;}
    public void setScheduleTime(LocalDateTime scheduleTime){this.scheduleTime.set(scheduleTime);}

    public int getCapacity() {return capacity.get();}
    public IntegerProperty capacityProperty() {return capacity;}
    public void setCapacity(int capacity){this.capacity.set(capacity);}

    public String getStatus(){return status.get();}
    public StringProperty statusProperty() {return status;}
    public void setStatus(String status) {this.status.set(status);}
}
