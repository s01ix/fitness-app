package com.example.fitnessapp.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.IntegerProperty;

public class CampaignPass {

    private final IntegerProperty passTypeId = new SimpleIntegerProperty();
    private final IntegerProperty campaignId = new SimpleIntegerProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();

    public CampaignPass(int id, int campaignId, int passTypeId) {
        this.id.set(id);
        this.campaignId.set(campaignId);
        this.passTypeId.set(passTypeId);
    }

    public CampaignPass(int campaignId, int passTypeId) {
        this.campaignId.set(campaignId);
        this.passTypeId.set(passTypeId);
    }

    public CampaignPass() {}

    public void setPassTypeId(int passTypeId) {
        this.passTypeId.set(passTypeId);
    }

    public int getPassTypeId() {
        return passTypeId.get();
    }

    public IntegerProperty passTypeIdProperty() {
        return passTypeId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId.set(campaignId);
    }

    public int getCampaignId() {
        return campaignId.get();
    }

    public IntegerProperty campaignIdProperty() {
        return campaignId;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }
}