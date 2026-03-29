package com.example.fitnessapp.model;

import javafx.beans.property.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty gymPassId = new SimpleIntegerProperty();
    private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> paymentDate = new SimpleObjectProperty<>();
    private final StringProperty method = new SimpleStringProperty();

    public Payment() {}

    public Payment(int id, int gymPassId, BigDecimal amount, LocalDateTime paymentDate, String method) {
        this.id.set(id);
        this.gymPassId.set(gymPassId);
        this.amount.set(amount);
        this.paymentDate.set(paymentDate);
        this.method.set(method);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getGymPassId() { return gymPassId.get(); }
    public IntegerProperty gymPassIdProperty() { return gymPassId; }
    public void setGymPassId(int gymPassId) { this.gymPassId.set(gymPassId); }

    public BigDecimal getAmount() { return amount.get(); }
    public ObjectProperty<BigDecimal> amountProperty() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount.set(amount); }

    public LocalDateTime getPaymentDate() { return paymentDate.get(); }
    public ObjectProperty<LocalDateTime> paymentDateProperty() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate.set(paymentDate); }

    public String getMethod() { return method.get(); }
    public StringProperty methodProperty() { return method; }
    public void setMethod(String method) { this.method.set(method); }
}