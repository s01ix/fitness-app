package com.example.fitnessapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Club {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty openingHours = new SimpleStringProperty();

    public Club() {}

    public Club(int id, String name, String address, String openingHours) {
        this.id.set(id);
        this.name.set(name);
        this.address.set(address);
        this.openingHours.set(openingHours);
    }

    public Club(String name, String address, String openingHours) {
        this.name.set(name);
        this.address.set(address);
        this.openingHours.set(openingHours);
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getOpeningHours() {
        return openingHours.get();
    }

    public StringProperty openingHoursProperty() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours.set(openingHours);
    }
}
