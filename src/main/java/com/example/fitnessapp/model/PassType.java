package com.example.fitnessapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;

public class PassType {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> basePrice = new SimpleObjectProperty<>();

    public PassType() {
    }

    public PassType(int id, String name, BigDecimal basePrice) {
        this.id.set(id);
        this.name.set(name);
        this.basePrice.set(basePrice);
    }

    public PassType(String name, BigDecimal basePrice) {
        this.name.set(name);
        this.basePrice.set(basePrice);
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

    public BigDecimal getBasePrice() {
        return basePrice.get();
    }

    public ObjectProperty<BigDecimal> basePriceProperty() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice.set(basePrice);
    }
}