package com.example.fitnessapp.model;

import javafx.beans.property.*;

public class GymUser {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty pesel = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public GymUser() {}

    public GymUser(int id, String pesel, String firstName, String lastName, String email, String passwordHash, String role, String status){
        this.id.set(id);
        this.pesel.set(pesel);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.email.set(email);
        this.passwordHash.set(passwordHash);
        this.role.set(role);
        this.status.set(status);
    }

    public int getId() {return id.get();}
    public IntegerProperty idProperty() {return id;}
    public void setId(int id) {this.id.set(id);}

    public String getPesel() {return pesel.get();}
    public StringProperty peselProperty() {return pesel;}
    public void setPesel(String pesel) {this.pesel.set(pesel);}

    public String getFirstName() {return firstName.get();}
    public StringProperty firstNameProperty() {return firstName;}
    public void setFirstName(String firstName) {this.firstName.set(firstName);}

    public String getLastName() {return lastName.get();}
    public StringProperty lastNameProperty() {return lastName;}
    public void setLastName(String lastName) {this.lastName.set(lastName);}

    public String getEmail() {return email.get();}
    public StringProperty emailProperty() {return email;}
    public void setEmail(String email) {this.email.set(email);}

    public String getPasswordHash() {return passwordHash.get();}
    public StringProperty passwordHashProperty() {return passwordHash;}
    public void setPasswordHash(String passwordHash) {this.passwordHash.set(passwordHash);}

    public String getRole() {return role.get();}
    public StringProperty roleProperty() {return role;}
    public void setRole(String role) {this.role.set(role);}

    public String getStatus() {return status.get();}
    public StringProperty statusProperty() {return status;}
    public void setStatus(String status) {this.status.set(status);}
}
