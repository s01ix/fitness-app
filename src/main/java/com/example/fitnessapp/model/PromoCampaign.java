package com.example.fitnessapp.model;

import java.time.LocalDate;
import java.math.BigDecimal;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;

public class PromoCampaign {

    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<BigDecimal> budget = new SimpleObjectProperty<>();
    private final StringProperty targetGroup = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();

    public PromoCampaign(int id, String name, String targetGroup,
                         BigDecimal budget, LocalDate startDate, LocalDate endDate) {
        this.id.set(id);
        this.name.set(name);
        this.targetGroup.set(targetGroup);
        this.budget.set(budget);
        this.startDate.set(startDate);
        this.endDate.set(endDate);
    }

    public PromoCampaign(String name, String targetGroup,
                         BigDecimal budget, LocalDate startDate, LocalDate endDate) {
        this.name.set(name);
        this.targetGroup.set(targetGroup);
        this.budget.set(budget);
        this.startDate.set(startDate);
        this.endDate.set(endDate);
    }

    public PromoCampaign() {
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public void setBudget(BigDecimal budget) {
        this.budget.set(budget);
    }

    public BigDecimal getBudget() {
        return budget.get();
    }

    public ObjectProperty<BigDecimal> budgetProperty() {
        return budget;
    }

    public void setTargetGroup(String targetGroup) {
        this.targetGroup.set(targetGroup);
    }

    public String getTargetGroup() {
        return targetGroup.get();
    }

    public StringProperty targetGroupProperty() {
        return targetGroup;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
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