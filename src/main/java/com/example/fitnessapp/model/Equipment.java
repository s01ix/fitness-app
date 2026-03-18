package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Equipment {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty club_id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> lastInspectionDate = new SimpleObjectProperty<>();

    public Equipment() {}

    public Equipment(int id, int club_id, String name, String status, LocalDate lastInspectionDate){
        this.id.set(id);
        this.club_id.set(club_id);
        this.name.set(name);
        this.status.set(status);
        this.lastInspectionDate.set(lastInspectionDate);
    }

    public int getId() {return id.get();}
    public IntegerProperty idProperty() {return id;}
    public void setId(int id) {this.id.set(id);}

    public int getClubId() {return club_id.get();}
    public IntegerProperty clubIdProperty() {return club_id;}
    public void setClubId(int clubId) {this.club_id.set(clubId);}

    public String getName() {return name.get();}
    public StringProperty nameProperty() {return name;}
    public void setName(String name) {this.name.set(name);}

    public String getStatus() {return status.get();}
    public StringProperty statusProperty() {return status;};
    public void setStatus(String status) {this.status.set(status);}

    public LocalDate getLastInspectionDate() {return lastInspectionDate.get();}
    public ObjectProperty<LocalDate> lastInspectionDateProperty() {return lastInspectionDate;}
    public void setLastInspectionDate(LocalDate date) {this.lastInspectionDate.set(date);}
}
