package com.example.fitnessapp.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.math.BigDecimal;

public class Discount {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty campaignId = new SimpleIntegerProperty();
    private final StringProperty discountType = new SimpleStringProperty();
    private final ObjectProperty<BigDecimal> discountValue = new SimpleObjectProperty<>();

    public Discount() {
    }

    public Discount(int id, int campaignId, String discountType, BigDecimal discountValue) {
        this.id.set(id);
        this.campaignId.set(campaignId);
        this.discountType.set(discountType);
        this.discountValue.set(discountValue);
    }

    public Discount(int campaignId, String discountType, BigDecimal discountValue) {
        this.campaignId.set(campaignId);
        this.discountType.set(discountType);
        this.discountValue.set(discountValue);
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

    public int getCampaignId() {
        return campaignId.get();
    }

    public IntegerProperty campaignIdProperty() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId.set(campaignId);
    }

    public String getDiscountType() {
        return discountType.get();
    }

    public StringProperty discountTypeProperty() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType.set(discountType);
    }

    public BigDecimal getDiscountValue() {
        return discountValue.get();
    }

    public ObjectProperty<BigDecimal> discountValueProperty() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue.set(discountValue);
    }
}