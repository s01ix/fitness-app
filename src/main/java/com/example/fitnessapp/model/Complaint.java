package com.example.fitnessapp.model;

import javafx.beans.property.*;

public class Complaint {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty authorId = new SimpleIntegerProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Complaint() {}

    public Complaint(int id, int authorId, String description, String status) {
        this.id.set(id);
        this.authorId.set(authorId);
        this.description.set(description);
        this.status.set(status);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    public void setId(int id) { this.id.set(id); }

    public int getAuthorId() { return authorId.get(); }
    public IntegerProperty authorIdProperty() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId.set(authorId); }

    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String description) { this.description.set(description); }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    public void setStatus(String status) { this.status.set(status); }
}