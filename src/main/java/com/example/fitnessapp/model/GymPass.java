package com.example.fitnessapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GymPass {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty userId = new SimpleIntegerProperty();
    private final IntegerProperty passTypeId = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> price = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> purchaseDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> expirationDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();

    public GymPass() {
    }

    public GymPass(int id, int userId, int passTypeId, BigDecimal price, LocalDate purchaseDate, LocalDate expirationDate, String status) {
        this.id.set(id);
        this.userId.set(userId);
        this.passTypeId.set(passTypeId);
        this.price.set(price);
        this.purchaseDate.set(purchaseDate);
        this.expirationDate.set(expirationDate);
        this.status.set(status);
    }

    public GymPass(int userId, int passTypeId, BigDecimal price, LocalDate purchaseDate, LocalDate expirationDate, String status) {
        this.userId.set(userId);
        this.passTypeId.set(passTypeId);
        this.price.set(price);
        this.purchaseDate.set(purchaseDate);
        this.expirationDate.set(expirationDate);
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

    public int getUserId() {
        return userId.get();
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public int getPassTypeId() {
        return passTypeId.get();
    }

    public IntegerProperty passTypeIdProperty() {
        return passTypeId;
    }

    public void setPassTypeId(int passTypeId) {
        this.passTypeId.set(passTypeId);
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate.get();
    }

    public ObjectProperty<LocalDate> purchaseDateProperty() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate.set(purchaseDate);
    }

    public LocalDate getExpirationDate() {
        return expirationDate.get();
    }

    public ObjectProperty<LocalDate> expirationDateProperty() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate.set(expirationDate);
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